package com.github.drakescraft_labs.labupdate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Comprueba el último release de {@code SlimefunNewHorizons/drakes-slimefun-labs} y, si hay un asset
 * más nuevo que la versión en ejecución, deja <strong>un único</strong> JAR del addon en
 * {@code updates/} (Paper/Bukkit).
 * <p>
 * Los releases del monorepo adjuntan <strong>un {@code .jar} por addon</strong> en el mismo
 * release (API de GitHub): solo se descarga el asset que coincide con este plugin. Los releases
 * antiguos que solo tengan {@code monorepo-plugins.zip} siguen soportados: se usa un temporal,
 * se extrae una sola entrada bajo {@code monorepo-jars/} y se borra el ZIP con reintentos.
 * <p>
 * Desactivar: propiedad del sistema {@code drakes.lab.autoupdate=false}, variable de entorno
 * {@code DRAKES_LAB_AUTOUPDATE=0}, o {@code drakes.lab.autoupdate.disable=true}.
 * Opcional: {@code GITHUB_TOKEN} para aumentar el límite de la API.
 */
public final class DrakesLabsReleaseUpdate {

    private static final String DEFAULT_REPO = "SlimefunNewHorizons/drakes-slimefun-labs";
    private static final String USER_AGENT = "DrakesLabs-AutoUpdate/1.2";
    private static final String MONOREPO_ZIP_NAME = "monorepo-plugins.zip";
    private static final String MONOREPO_ENTRY_PREFIX = "monorepo-jars/";
    private static final int DELETE_RETRIES = 8;
    private static final long DELETE_RETRY_MS = 75L;

    private DrakesLabsReleaseUpdate() {}

    /**
     * Lanza una comprobación asíncrona al habilitar el plugin.
     *
     * @param plugin           plugin principal (normalmente {@code this})
     * @param mavenArtifactId  {@code <artifactId>} del módulo Maven (p. ej. {@code ExoticGarden-drake})
     */
    public static void schedule(JavaPlugin plugin, String mavenArtifactId) {
        schedule(plugin, mavenArtifactId, DEFAULT_REPO);
    }

    /**
     * @param githubRepo repositorio {@code owner/name} del que leer releases (p. ej. addon standalone)
     */
    public static void schedule(JavaPlugin plugin, String mavenArtifactId, String githubRepo) {
        if (mavenArtifactId == null || mavenArtifactId.isBlank()) {
            return;
        }
        if (isDisabled()) {
            return;
        }
        final String repo = (githubRepo == null || githubRepo.isBlank()) ? DEFAULT_REPO : githubRepo.trim();
        final String token = githubToken();
        final String aid = mavenArtifactId.trim();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> run(plugin, aid, repo, token));
    }

    private static boolean isDisabled() {
        if (Boolean.getBoolean("drakes.lab.autoupdate.disable")) {
            return true;
        }
        String prop = System.getProperty("drakes.lab.autoupdate");
        if (prop != null && prop.equalsIgnoreCase("false")) {
            return true;
        }
        String env = System.getenv("DRAKES_LAB_AUTOUPDATE");
        return env != null && (env.equals("0") || env.equalsIgnoreCase("false") || env.equalsIgnoreCase("off"));
    }

    private static String githubToken() {
        String t = System.getenv("GITHUB_TOKEN");
        return t != null && !t.isBlank() ? t.trim() : null;
    }

    private static void run(JavaPlugin plugin, String mavenArtifactId, String githubRepo, String token) {
        File zipScratch = null;
        try {
            String apiLatest = "https://api.github.com/repos/" + githubRepo + "/releases/latest";
            String json = httpGetJson(apiLatest, token);
            if (json == null || json.isBlank()) {
                return;
            }
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray assets = root.getAsJsonArray("assets");
            if (assets == null || assets.size() == 0) {
                return;
            }
            String pluginName = plugin.getName();
            String currentRaw = plugin.getDescription().getVersion();
            if (currentRaw == null || currentRaw.isBlank()) {
                return;
            }
            ComparableVersion current = new ComparableVersion(stripDevPrefix(currentRaw));

            List<UpdateCandidate> candidates = new ArrayList<>();
            collectDirectJarCandidates(assets, mavenArtifactId, pluginName, candidates);

            UpdateCandidate jarBest = pickNewest(candidates);
            boolean jarAlreadyNewer = jarBest != null && jarBest.version.compareTo(current) > 0;

            String zipUrl = findPreferredZipAssetUrl(assets);
            if (zipUrl != null && !jarAlreadyNewer) {
                zipScratch = Files.createTempFile("drakes-labs-monorepo-", ".zip").toFile();
                try {
                    downloadToFile(zipUrl, zipScratch, token);
                } catch (IOException e) {
                    forceDelete(zipScratch);
                    zipScratch = null;
                    throw e;
                }
                collectZipJarCandidates(zipScratch, mavenArtifactId, pluginName, candidates);
            }

            UpdateCandidate best = pickNewest(candidates);
            if (best == null || best.version.compareTo(current) <= 0) {
                return;
            }

            File updateDir = Bukkit.getUpdateFolderFile();
            if (!updateDir.isDirectory() && !updateDir.mkdirs()) {
                plugin.getLogger().warning("[DrakesLabs] No se pudo crear la carpeta updates/");
                return;
            }
            File target = new File(updateDir, best.outputJarFilename);
            if (best.directDownloadUrl != null) {
                downloadToFile(best.directDownloadUrl, target, token);
                plugin.getLogger().info("[DrakesLabs] Actualización descargada: " + best.outputJarFilename + " → updates/");
            } else if (best.zipScratch != null && best.zipEntryPath != null) {
                extractZipEntryAtomically(best.zipScratch, best.zipEntryPath, target);
                plugin.getLogger().info("[DrakesLabs] Actualización extraída del ZIP monorepo (una sola entrada): " + best.outputJarFilename + " → updates/");
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.FINE, "[DrakesLabs] Comprobación de release omitida", e);
        } finally {
            forceDelete(zipScratch);
        }
    }

    private static void forceDelete(File f) {
        if (f == null || !f.exists()) {
            return;
        }
        for (int i = 0; i < DELETE_RETRIES; i++) {
            if (f.delete()) {
                return;
            }
            try {
                Thread.sleep(DELETE_RETRY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        try {
            Files.deleteIfExists(f.toPath());
        } catch (IOException ignored) {
            // sin deleteOnExit: evita acumular miles de rutas en JVMs largas
        }
    }

    private static final class UpdateCandidate {
        final ComparableVersion version;
        final String outputJarFilename;
        final String directDownloadUrl;
        final File zipScratch;
        final String zipEntryPath;

        UpdateCandidate(
                ComparableVersion version,
                String outputJarFilename,
                String directDownloadUrl,
                File zipScratch,
                String zipEntryPath) {
            this.version = version;
            this.outputJarFilename = outputJarFilename;
            this.directDownloadUrl = directDownloadUrl;
            this.zipScratch = zipScratch;
            this.zipEntryPath = zipEntryPath;
        }
    }

    private static void collectDirectJarCandidates(
            JsonArray assets,
            String mavenArtifactId,
            String pluginName,
            List<UpdateCandidate> out) {
        for (JsonElement el : assets) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject a = el.getAsJsonObject();
            if (!a.has("name") || !a.has("browser_download_url")) {
                continue;
            }
            String name = a.get("name").getAsString();
            if (!name.toLowerCase(Locale.ROOT).endsWith(".jar")) {
                continue;
            }
            if (!assetMatches(name, mavenArtifactId, pluginName)) {
                continue;
            }
            String verStr = guessVersionFromAssetName(name, mavenArtifactId, pluginName);
            ComparableVersion ver = new ComparableVersion(stripDevPrefix(verStr));
            String url = a.get("browser_download_url").getAsString();
            out.add(new UpdateCandidate(ver, name, url, null, null));
        }
    }

    private static String findPreferredZipAssetUrl(JsonArray assets) {
        String fallback = null;
        for (JsonElement el : assets) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject a = el.getAsJsonObject();
            if (!a.has("name") || !a.has("browser_download_url")) {
                continue;
            }
            String name = a.get("name").getAsString();
            if (!name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                continue;
            }
            String url = a.get("browser_download_url").getAsString();
            if (MONOREPO_ZIP_NAME.equalsIgnoreCase(name)) {
                return url;
            }
            if (fallback == null) {
                fallback = url;
            }
        }
        return fallback;
    }

    /**
     * Solo considera entradas {@code monorepo-jars/*.jar} que parezcan plugins (misma heurística
     * que {@code collect_monorepo_jars.py} para no mezclar basura de tooling en el ZIP).
     */
    private static void collectZipJarCandidates(
            File zipFile,
            String mavenArtifactId,
            String pluginName,
            List<UpdateCandidate> out) throws IOException {
        try (ZipFile zf = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> en = zf.entries();
            while (en.hasMoreElements()) {
                ZipEntry entry = en.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String full = entry.getName().replace('\\', '/');
                if (full.contains("..")) {
                    continue;
                }
                if (!full.regionMatches(true, 0, MONOREPO_ENTRY_PREFIX, 0, MONOREPO_ENTRY_PREFIX.length())) {
                    continue;
                }
                int slash = full.lastIndexOf('/');
                String base = slash >= 0 ? full.substring(slash + 1) : full;
                if (!base.toLowerCase(Locale.ROOT).endsWith(".jar")) {
                    continue;
                }
                if (skipZipJarName(base)) {
                    continue;
                }
                if (!assetMatches(base, mavenArtifactId, pluginName)) {
                    continue;
                }
                String verStr = guessVersionFromAssetName(base, mavenArtifactId, pluginName);
                ComparableVersion ver = new ComparableVersion(stripDevPrefix(verStr));
                out.add(new UpdateCandidate(ver, base, null, zipFile, full));
            }
        }
    }

    private static boolean skipZipJarName(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        if (n.startsWith("original-")) {
            return true;
        }
        return n.endsWith("-sources.jar")
                || n.endsWith("-javadoc.jar")
                || n.endsWith("-tests.jar")
                || n.endsWith("-plain.jar");
    }

    private static UpdateCandidate pickNewest(List<UpdateCandidate> candidates) {
        UpdateCandidate best = null;
        for (UpdateCandidate c : candidates) {
            if (best == null || c.version.compareTo(best.version) > 0) {
                best = c;
            }
        }
        return best;
    }

    /**
     * Escribe solo la entrada indicada; primero a un temporal y luego {@code REPLACE_ATOMIC}
     * para no dejar un JAR a medias en {@code updates/}.
     */
    private static void extractZipEntryAtomically(File zipFile, String entryPath, File finalDest) throws IOException {
        File dir = finalDest.getParentFile();
        if (dir == null) {
            throw new IOException("Ruta de destino inválida");
        }
        File tmp = File.createTempFile(".drakes-extract-", ".jar.tmp", dir);
        try {
            try (ZipFile zf = new ZipFile(zipFile);
                    InputStream in = zf.getInputStream(zf.getEntry(entryPath));
                    FileOutputStream out = new FileOutputStream(tmp)) {
                if (in == null) {
                    throw new IOException("Entrada ZIP ausente: " + entryPath);
                }
                in.transferTo(out);
            }
            Files.move(tmp.toPath(), finalDest.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            tmp = null;
        } finally {
            if (tmp != null && tmp.exists()) {
                forceDelete(tmp);
            }
        }
    }

    private static String stripDevPrefix(String v) {
        String s = v.trim();
        if (s.regionMatches(true, 0, "DEV - ", 0, 6)) {
            return s.substring(6).trim();
        }
        return s;
    }

    private static boolean assetMatches(String assetName, String mavenArtifactId, String pluginName) {
        String keyA = compactKey(mavenArtifactId);
        String keyP = compactKey(pluginName);
        String keyAsset = compactKey(assetName);
        if (!keyA.isEmpty() && keyAsset.contains(keyA)) {
            return true;
        }
        return !keyP.isEmpty() && keyAsset.contains(keyP);
    }

    private static String compactKey(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                b.append(Character.toLowerCase(c));
            }
        }
        return b.toString();
    }

    private static String guessVersionFromAssetName(String assetName, String mavenArtifactId, String pluginName) {
        String base = assetName.endsWith(".jar") ? assetName.substring(0, assetName.length() - 4) : assetName;
        Matcher m = Pattern.compile("(?i)\\s+v\\s*(.+)$").matcher(base);
        if (m.find()) {
            return m.group(1).trim();
        }
        String v = substringAfterPrefixIgnoreCase(base, mavenArtifactId + "-");
        if (v != null) {
            return v;
        }
        v = substringAfterPrefixIgnoreCase(base, pluginName + "-");
        if (v != null) {
            return v;
        }
        int last = base.lastIndexOf('-');
        if (last > 0 && last < base.length() - 1) {
            return base.substring(last + 1);
        }
        return base;
    }

    private static String substringAfterPrefixIgnoreCase(String base, String prefix) {
        int idx = base.toLowerCase(Locale.ROOT).indexOf(prefix.toLowerCase(Locale.ROOT));
        if (idx < 0) {
            return null;
        }
        return base.substring(idx + prefix.length());
    }

    private static String httpGetJson(String url, String token) throws IOException {
        HttpURLConnection c = (HttpURLConnection) URI.create(url).toURL().openConnection();
        c.setRequestProperty("Accept", "application/vnd.github+json");
        c.setRequestProperty("User-Agent", USER_AGENT);
        c.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
        if (token != null) {
            c.setRequestProperty("Authorization", "Bearer " + token);
        }
        c.setConnectTimeout(15000);
        c.setReadTimeout(25000);
        int code = c.getResponseCode();
        InputStream in = code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream();
        if (in == null) {
            return null;
        }
        byte[] body = in.readAllBytes();
        in.close();
        if (code < 200 || code >= 300) {
            return null;
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    private static void downloadToFile(String url, File dest, String token) throws IOException {
        HttpURLConnection c = (HttpURLConnection) URI.create(url).toURL().openConnection();
        c.setRequestProperty("Accept", "application/octet-stream");
        c.setRequestProperty("User-Agent", USER_AGENT);
        if (token != null) {
            c.setRequestProperty("Authorization", "Bearer " + token);
        }
        c.setConnectTimeout(20000);
        c.setReadTimeout(120000);
        c.setInstanceFollowRedirects(true);
        try (InputStream raw = c.getInputStream();
                BufferedInputStream in = new BufferedInputStream(raw);
                FileOutputStream out = new FileOutputStream(dest)) {
            in.transferTo(out);
        }
    }
}
