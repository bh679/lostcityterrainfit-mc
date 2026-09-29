"""Draws the Lost City Terrain Fit icon: a ruined skyscraper settled into a grassy hillside, flooded at its foot.

Pixel art on a 32x32 grid, scaled to 512x512 with a pure-Python PNG writer (no Pillow needed).
Run: python3 art/make_icon.py  ->  art/icon.png
"""
import math
import os
import struct
import zlib

N = 32
SCALE = 16
CORNER_RADIUS = 72  # px at 512: rounded corners, transparent outside, anti-aliased edge

SKY_TOP = (92, 150, 222)
SKY_LOW = (176, 214, 240)
GRASS = (94, 168, 60)
GRASS_DARK = (70, 132, 44)
DIRT = (134, 96, 60)
DIRT_DARK = (108, 76, 48)
STONE = (120, 120, 124)
WATER = (52, 108, 196)
WATER_LIGHT = (86, 146, 222)
CONCRETE = (150, 152, 158)
CONCRETE_DARK = (112, 114, 122)
WINDOW = (40, 46, 60)
WINDOW_LIT = (236, 196, 96)
VINE = (60, 126, 40)
OUTLINE = (30, 34, 44)


def hill_top(x):
    """Ground surface row for column x: low on the left, rising to the right and into the tower's side."""
    return int(round(28 - 13 * (1 / (1 + math.exp(-(x - 17) / 3.0)))))


def blend(c, tint, a):
    return tuple(int(c[i] * (1 - a) + tint[i] * a) for i in range(3))


def draw():
    img = [[None] * N for _ in range(N)]
    for y in range(N):
        t = y / (N - 1)
        c = tuple(int(SKY_TOP[i] + (SKY_LOW[i] - SKY_TOP[i]) * t) for i in range(3))
        for x in range(N):
            img[y][x] = c
    for cx, cy, w in ((3, 5, 6), (23, 3, 5)):
        for x in range(cx, cx + w):
            img[cy][x] = (240, 246, 252)
        for x in range(cx + 1, cx + w - 1):
            img[cy - 1][x] = (240, 246, 252)

    # the tower: columns 9..19, ragged ruined top, standing on the seabed at row 29
    tx0, tx1 = 9, 19
    tower_top = [8, 6, 5, 5, 7, 6, 4, 4, 6, 9, 11]
    for x in range(tx0, tx1 + 1):
        top = tower_top[x - tx0]
        for y in range(top, 30):
            img[y][x] = CONCRETE_DARK if x in (tx0, tx1) else CONCRETE
        img[top][x] = OUTLINE
    for y in range(8, 29, 3):
        for x in (tx0 + 2, tx0 + 5, tx0 + 8):
            if y > tower_top[x - tx0] + 1:
                lit = (x, y) in ((11, 17), (17, 11))
                img[y][x] = WINDOW_LIT if lit else WINDOW
                img[y + 1][x] = WINDOW_LIT if lit else WINDOW
    # vines hanging from the broken top
    for x, y0, y1 in ((tx0 + 1, 10, 21), (tx0 + 4, 8, 14), (tx0 + 7, 7, 12)):
        for y in range(y0, y1):
            if (y + x) % 4 != 0:
                img[y][x] = VINE

    # ground: the hill climbs in over the tower's lower floors on the right
    for x in range(N):
        top = hill_top(x)
        for y in range(top, N):
            d = y - top
            if d == 0:
                img[y][x] = GRASS if x % 3 else GRASS_DARK
            elif d < 3:
                img[y][x] = DIRT if (x + y) % 4 else DIRT_DARK
            else:
                img[y][x] = STONE if (x * 3 + y) % 5 else DIRT_DARK
        if top < 29 and x > tx1:
            img[top - 1][x] = img[top - 1][x] if (x % 5) else (58, 140, 50)  # a tuft

    # water floods the low side, tinting the tower's foot rather than hiding it
    water_level = 24
    for x in range(N):
        top = hill_top(x)
        for y in range(water_level, top):
            base = img[y][x]
            if tx0 <= x <= tx1:
                img[y][x] = blend(base, WATER, 0.55)
            else:
                img[y][x] = WATER_LIGHT if y == water_level else WATER
        if water_level < top:
            img[water_level][x] = blend(img[water_level][x], WATER_LIGHT, 0.6)
    return img


def corner_alpha(x, y, size, radius):
    """Coverage (0-255) of pixel (x, y) by a rounded square, sampled 4x4 for a smooth edge."""
    cx = min(max(x + 0.5, radius), size - radius)
    cy = min(max(y + 0.5, radius), size - radius)
    if abs(x + 0.5 - cx) < 1e-9 and abs(y + 0.5 - cy) < 1e-9:
        return 255
    hits = 0
    for i in range(4):
        for j in range(4):
            px, py = x + (i + 0.5) / 4, y + (j + 0.5) / 4
            if (px - cx) ** 2 + (py - cy) ** 2 <= radius * radius:
                hits += 1
    return round(255 * hits / 16)


def write_png(path, img):
    size = N * SCALE
    raw = bytearray()
    for y in range(size):
        raw.append(0)
        row = img[y // SCALE]
        for x in range(size):
            r, g, b = row[x // SCALE]
            raw += bytes((r, g, b, corner_alpha(x, y, size, CORNER_RADIUS)))

    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xFFFFFFFF)

    png = b'\x89PNG\r\n\x1a\n'
    png += chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 6, 0, 0, 0))
    png += chunk(b'IDAT', zlib.compress(bytes(raw), 9))
    png += chunk(b'IEND', b'')
    with open(path, 'wb') as f:
        f.write(png)


if __name__ == '__main__':
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'icon.png')
    write_png(out, draw())
    print(out)
