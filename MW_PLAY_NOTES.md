# Implementation notes

The first pass deliberately avoids modifying the streaming core. The next implementation pass should locate the upstream navigation, Board/Home, MetaDetails and Player component boundaries and apply the UI spec incrementally. This minimizes regressions and keeps changes reviewable.

Before replacing binary app icons, export the approved MW mark to the exact PNG/ICO dimensions required by the existing PWA/favicon pipeline. SVG source assets are already included in `images/`.
