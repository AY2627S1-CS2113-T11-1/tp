"""Renders Mermaid diagrams in docs/diagrams to SVGs in docs/images.

Usage (from the repository root, with the Mermaid CLI installed: npm install -g @mermaid-js/mermaid-cli):
    python3 docs/diagrams/render.py product-delete

Mermaid's `destroy` statement ends a participant's lifeline but does not draw the UML destroy
marker (an X), so after mmdc renders a diagram this script draws an X wherever a lifeline was
cut short. Only the diagrams named on the command line are rendered, because some SVGs in
docs/images were drawn by hand and must not be overwritten.
"""

import re
import shutil
import subprocess
import sys
from pathlib import Path

DIAGRAMS = Path(__file__).resolve().parent
IMAGES = DIAGRAMS.parent / "images"

# Mermaid draws a normal lifeline down to a fixed far-off y; a destroyed one stops at the destroy point.
LIFELINE = re.compile(r'<line[^>]*x1="([\d.]+)"[^>]*y2="([\d.]+)"[^>]*class="actor-line[^"]*"[^>]*>')
ACTIVATION = re.compile(r'<rect x="([\d.]+)" y="([\d.]+)"[^>]*width="([\d.]+)" height="([\d.]+)"'
                        r'[^>]*class="activation\d"')
X_HALF_SIZE = 9


def add_destroy_markers(svg):
    """Draws an X at the end of every lifeline that is shorter than the others, growing the
    canvas if an X would fall past its bottom edge."""
    lifelines = [(float(x), float(y2)) for x, y2 in LIFELINE.findall(svg)]
    if not lifelines:
        return svg
    full_length = max(y2 for _, y2 in lifelines)
    markers = []
    lowest = 0
    for x, y2 in lifelines:
        if y2 >= full_length:
            continue
        # The lifeline is cut at the last message, but Mermaid runs the final activation bar past it.
        # UML has the last return leave from the bottom of the bar, so end the bar at the cut.
        for match in ACTIVATION.finditer(svg):
            bar_x, bar_y, bar_width, bar_height = map(float, match.groups())
            if abs(bar_x + bar_width / 2 - x) < 1 and bar_y < y2 < bar_y + bar_height:
                trimmed = match.group(0).replace(f'height="{match.group(4)}"', f'height="{y2 - bar_y}"')
                svg = svg.replace(match.group(0), trimmed, 1)
        end = y2 + X_HALF_SIZE
        markers.append(
            f'<path d="M{x - X_HALF_SIZE},{end - X_HALF_SIZE} L{x + X_HALF_SIZE},{end + X_HALF_SIZE} '
            f'M{x + X_HALF_SIZE},{end - X_HALF_SIZE} L{x - X_HALF_SIZE},{end + X_HALF_SIZE}" '
            f'stroke="#40566f" stroke-width="2" fill="none"/>')
        lowest = max(lowest, end + X_HALF_SIZE)
    svg = svg.replace("</svg>", "".join(markers) + "</svg>", 1)

    view_box = re.search(r'viewBox="([-\d.]+) ([-\d.]+) ([\d.]+) ([\d.]+)"', svg)
    if view_box:
        min_x, min_y, width, height = map(float, view_box.groups())
        needed = lowest + 10 - min_y
        if needed > height:
            svg = svg.replace(view_box.group(0), f'viewBox="{min_x} {min_y} {width} {needed}"', 1)
    return svg


def render(name):
    source = DIAGRAMS / f"{name}.mmd"
    target = IMAGES / f"{name}.svg"
    # htmlLabels=false keeps all labels as plain SVG text, which renders when the DG embeds the SVG
    # as an <img>; HTML labels inside <foreignObject> do not.
    config = DIAGRAMS / "mermaid-config.json"
    # shutil.which also finds mmdc.cmd, the name npm installs it under on Windows.
    mmdc = shutil.which("mmdc")
    if mmdc is None:
        sys.exit("mmdc not found: install it with npm install -g @mermaid-js/mermaid-cli")
    subprocess.run([mmdc, "-i", str(source), "-o", str(target), "-c", str(config), "-b", "white"],
                   check=True)
    svg = target.read_text(encoding="utf-8")
    target.write_text(add_destroy_markers(svg), encoding="utf-8")
    print(f"Rendered {target.relative_to(DIAGRAMS.parent.parent)}")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    for diagram in sys.argv[1:]:
        render(diagram)
