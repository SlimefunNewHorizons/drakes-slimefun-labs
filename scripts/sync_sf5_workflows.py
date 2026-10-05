from pathlib import Path
from urllib.request import urlopen


ROOT = Path(__file__).resolve().parents[1]
WORKFLOWS_DIR = ROOT / ".github" / "workflows"
BASE = "https://raw.githubusercontent.com/Slimefun5/Slimefun5/experimental/.github/workflows"

# Workflows de gestion/automatizacion que si aplican al monorepo Drake.
SELECTED = [
    "auto-approve.yml",
    "auto-squash.yml",
    "close-invalid-issues.yml",
    "duplicates.yml",
    "json-validator.yml",
    "label-resolved-issues.yml",
    "merge-conflicts.yml",
    "pr-labels.yml",
    "yaml-linter.yml",
]


def fetch(name: str) -> str:
    with urlopen(f"{BASE}/{name}") as resp:  # nosec B310 - URL fija y controlada
        return resp.read().decode("utf-8")


def transform(text: str) -> str:
    lines = text.splitlines()
    out = []
    name_replaced = False

    for line in lines:
        if line.startswith("name: ") and not name_replaced:
            out.append(f"name: Drake {line.removeprefix('name: ').strip()}")
            name_replaced = True
            continue

        line = line.replace("Slimefun5/Slimefun5", "SlimefunNewHorizons/drakes-slimefun-labs")
        line = line.replace("- stable", "- 1.21-latin")
        line = line.replace("- experimental", "- 1.21-latin")
        out.append(line)

    return "\n".join(out) + "\n"


def target_name(source_name: str) -> str:
    return f"drake-{source_name}"


def main() -> None:
    WORKFLOWS_DIR.mkdir(parents=True, exist_ok=True)

    for wf in SELECTED:
        content = transform(fetch(wf))
        path = WORKFLOWS_DIR / target_name(wf)
        path.write_text(content, encoding="utf-8")
        print(f"Generado: {path}")


if __name__ == "__main__":
    main()
