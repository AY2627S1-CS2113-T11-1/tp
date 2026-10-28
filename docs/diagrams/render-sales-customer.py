"""Render the five simple Mermaid sequence sources in the team's compact SVG style.

Run with Python from any directory. Supports participants and horizontal messages only;
unsupported statements fail explicitly rather than silently changing a diagram.
"""
from pathlib import Path
from html import escape
import re

ROOT = Path(__file__).resolve().parent
NAMES = ['sales-list', 'generate-receipts', 'customer-payment', 'order-details', 'command-help']

for name in NAMES:
    actors, messages = {}, []
    for line in (ROOT / (name + '.mmd')).read_text().splitlines():
        line = line.strip()
        if not line or line == 'sequenceDiagram' or line.startswith('%%'):
            continue
        actor = re.fullmatch(r'participant (\w+) as (.+)', line)
        message = re.fullmatch(r'(\w+)(-->>|->>)(\w+): (.+)', line)
        if actor:
            actors[actor[1]] = actor[2]
        elif message:
            messages.append(message.groups())
        else:
            raise ValueError(f'Unsupported diagram statement: {line}')
    width, height = len(actors) * 180 + 20, 115 + 48 * len(messages)
    positions = {key: 100 + i * 180 for i, key in enumerate(actors)}
    svg = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}" role="img" aria-labelledby="title desc">',
           f'  <title id="title">{name.replace("-", " ").title()} sequence</title>',
           '  <desc id="desc">Main successful execution flow. Validation and alternatives are described in the Developer Guide.</desc>',
           '  <defs>',
           '    <marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto"><path d="M0,0 L10,3 L0,6 Z" fill="#40566f"/></marker>',
           '    <style>',
           '      .head { fill:#eef4fb; stroke:#40566f; stroke-width:1.3; }',
           '      .name { fill:#1f2933; font:14px Arial,sans-serif; text-anchor:middle; }',
           '      .life { stroke:#8a98a8; stroke-width:1.2; stroke-dasharray:5 5; }',
           '      .msg { stroke:#40566f; stroke-width:1.3; marker-end:url(#arrow); }',
           '      .text { fill:#1f2933; font:13px Arial,sans-serif; text-anchor:middle; }',
           '    </style>', '  </defs>',
           f'  <rect width="{width}" height="{height}" fill="white"/>']
    for key, label in actors.items():
        x = positions[key]
        parts = [label[:-7], 'Command'] if label.endswith('Command') else [label]
        svg.append(f'  <rect class="head" x="{x-78}" y="20" width="156" height="48" rx="5"/>')
        spans = ''.join(f'<tspan x="{x}" y="{40+i*17 if len(parts)>1 else 49}">{escape(part)}</tspan>' for i, part in enumerate(parts))
        svg.append(f'  <text class="name">{spans}</text>')
        svg.append(f'  <line class="life" x1="{x}" y1="68" x2="{x}" y2="{height-15}"/>')
    for i, (start, arrow, end, label) in enumerate(messages):
        x1, x2, y = positions[start], positions[end], 110 + 48*i
        dash = ' stroke-dasharray="5 4"' if arrow == '-->>' else ''
        svg.append(f'  <line class="msg" x1="{x1}" y1="{y}" x2="{x2}" y2="{y}"{dash}/>')
        svg.append(f'  <text class="text" x="{(x1+x2)/2:g}" y="{y-9}">{escape(label)}</text>')
    svg.append('</svg>')
    (ROOT.parent / 'images' / (name + '.svg')).write_text('\n'.join(svg)+'\n', encoding='utf-8')
