# -*- coding: utf-8 -*-
"""
Extract the compiled English map from Fieldwatch source.

EnglishCopy.lines = buildLines() (LanguageCopy.kt) + ChromeLines.all (ChromeLines.kt)
LanguagePack.template() generates the exported English template from exactly that list,
so this extraction is equivalent to Settings -> Language -> Export English template.

The two .kt files are resolved in this order:
  1. next to this script;
  2. the vendored copy in upstream-v<version>/ (offline, shipped with this repo);
  3. downloaded from GitHub -- `gh api` first, then raw.githubusercontent.com.

So a fresh clone reproduces en_map.json with no network at all:

    python extract_en.py

Refresh against a newer upstream tag (needs `gh`, or access to raw.githubusercontent):

    python extract_en.py v1.1.32
"""
import json
import os
import re
import subprocess
import sys
import urllib.request

BASE = os.path.dirname(os.path.abspath(__file__))
# order matters: buildLines() first, then ChromeLines.all
FILES = ["LanguageCopy.kt", "ChromeLines.kt"]
UPSTREAM_DIR = "upstream-v1.1.31"
GH_PATH = "app/src/main/java/app/fieldwatch/domain/{name}"
RAW_URL = ("https://raw.githubusercontent.com/OffGridPete/Fieldwatch/"
           "{ref}/app/src/main/java/app/fieldwatch/domain/{name}")


def fetch_via_gh(ref, name):
    path = GH_PATH.format(name=name)
    out = subprocess.run(
        ["gh", "api", "repos/OffGridPete/Fieldwatch/contents/%s?ref=%s" % (path, ref),
         "--jq", ".content"],
        capture_output=True, text=True,
    )
    if out.returncode != 0 or not out.stdout.strip():
        return None
    import base64
    return base64.b64decode(out.stdout.strip())


def fetch_via_raw(ref, name):
    url = RAW_URL.format(ref=ref, name=name)
    req = urllib.request.Request(url, headers={"User-Agent": "fieldwatch-zh-extract"})
    with urllib.request.urlopen(req, timeout=30) as resp:
        return resp.read()


def fetch_sources(ref):
    """Make sure both .kt files sit next to this script."""
    vendored = os.path.join(BASE, UPSTREAM_DIR)
    for name in FILES:
        path = os.path.join(BASE, name)
        if os.path.exists(path):
            print("using local %s" % name, file=sys.stderr)
            continue
        vpath = os.path.join(vendored, name)
        if os.path.exists(vpath):
            with open(vpath, "rb") as src, open(path, "wb") as dst:
                dst.write(src.read())
            print("using vendored %s/%s" % (UPSTREAM_DIR, name), file=sys.stderr)
            continue
        data = None
        for fetcher in (fetch_via_gh, fetch_via_raw):
            try:
                data = fetcher(ref, name)
                if data:
                    print("fetched %s via %s" % (name, fetcher.__name__), file=sys.stderr)
                    break
            except Exception as exc:  # network or gh unavailable -> try the next
                print("%s failed for %s: %s" % (fetcher.__name__, name, exc), file=sys.stderr)
        if not data:
            raise SystemExit(
                "cannot obtain %s. Put it next to extract_en.py, or keep %s/." % (name, UPSTREAM_DIR))
        with open(path, "wb") as fh:
            fh.write(data)
        print("  -> %s (%d bytes)" % (name, len(data)), file=sys.stderr)


EN_CALL = re.compile(r'\ben\(')
SLOT_RE = re.compile(r'LanguageSlot\.([A-Z]+)')


def parse_string(s, i):
    """s[i] must be '"'. Returns (value, index_after_closing_quote)."""
    assert s[i] == '"', "not at string start: %d" % i
    i += 1
    out = []
    while i < len(s):
        c = s[i]
        if c == '\\':
            n = s[i + 1]
            if n == 'n':
                out.append('\n')
            elif n == 't':
                out.append('\t')
            elif n == 'r':
                out.append('\r')
            elif n == 'u':
                out.append(chr(int(s[i + 2:i + 6], 16)))
                i += 6
                continue
            else:
                out.append(n)
            i += 2
            continue
        if c == '"':
            return ''.join(out), i + 1
        out.append(c)
        i += 1
    raise ValueError("unterminated string literal")


def skip_ws(s, i):
    while i < len(s) and s[i] in ' \t\r\n':
        i += 1
    return i


def expect(s, i, ch, ctx):
    if i >= len(s) or s[i] != ch:
        raise ValueError("expected %r at %d, got %r near %r" % (ch, i, s[i:i + 1], ctx))
    return i + 1


def extract(path):
    s = open(path, encoding='utf-8').read()
    rows = []
    i = 0
    while True:
        m = EN_CALL.search(s, i)
        if not m:
            break
        j = skip_ws(s, m.end())
        # the helper definition `fun en(key: String, ...)` is skipped: no quote follows
        if j >= len(s) or s[j] != '"':
            i = m.end()
            continue
        key, j = parse_string(s, j)
        j = skip_ws(s, j)
        j = expect(s, j, ',', key)
        j = skip_ws(s, j)
        if s[j] != '"':
            raise ValueError("text arg not a literal for key %s" % key)
        text, j = parse_string(s, j)
        j = skip_ws(s, j)
        j = expect(s, j, ',', key)
        j = skip_ws(s, j)
        sm = SLOT_RE.match(s, j)
        if not sm:
            raise ValueError("slot not literal for key %s near %r" % (key, s[j:j + 40]))
        rows.append({"key": key, "text": text, "slot": sm.group(1)})
        i = sm.end()
    return rows


def cap_for(slot, text):
    """Mirror of EnglishCopy.cap(line). Kotlin .length == UTF-16 units; all BMP here."""
    if slot in ("TAB", "CLASSCHIP"):
        return 18
    if slot == "CONTROL":
        return max(1, min(48, len(text) * 2))
    if slot == "BODY":
        return min(700, max(80, len(text) * 3))
    raise ValueError("unknown slot %s" % slot)


KEY_RE = re.compile(r'^[a-z0-9]+(\.[a-z0-9]+)+$')
TOKEN_RE = re.compile(r'\{[A-Za-z][A-Za-z0-9_]*\}')
PLACE_RE = re.compile(r'\{([A-Za-z][A-Za-z0-9_]*)\}')


def placeholder_names(text):
    return set(PLACE_RE.findall(text))


def main():
    fetch_sources(sys.argv[1] if len(sys.argv) > 1 else "main")
    lines = []
    for f in FILES:
        rows = extract(os.path.join(BASE, f))
        print("%s: %d lines" % (f, len(rows)), file=sys.stderr)
        lines.extend(rows)

    # integrity checks mirroring EnglishCopy.init
    seen = set()
    for ln in lines:
        k = ln["key"]
        if k in seen:
            raise SystemExit("duplicate key: %s" % k)
        seen.add(k)
        if not KEY_RE.match(k):
            raise SystemExit("bad key format: %s" % k)
        # every '{' must start a well-formed token
        i = 0
        while i < len(ln["text"]):
            b = ln["text"].find('{', i)
            if b < 0:
                break
            m = TOKEN_RE.search(ln["text"], b)
            if not m or m.start() != b:
                raise SystemExit("bad placeholder in %s: %r" % (k, ln["text"]))
            i = m.end()

    out = []
    for ln in lines:
        out.append({
            "key": ln["key"],
            "text": ln["text"],
            "slot": ln["slot"],
            "cap": cap_for(ln["slot"], ln["text"]),
            "ph": sorted(placeholder_names(ln["text"])),
        })

    with open(os.path.join(BASE, "en_map.json"), "w", encoding="utf-8") as fh:
        json.dump(out, fh, ensure_ascii=False, indent=1)

    # stats
    from collections import Counter
    slots = Counter(r["slot"] for r in out)
    total_chars = sum(len(r["text"]) for r in out)
    with_ph = sum(1 for r in out if r["ph"])
    with_nl = sum(1 for r in out if "\n" in r["text"])
    print("TOTAL keys      : %d" % len(out), file=sys.stderr)
    print("by slot         : %s" % dict(slots), file=sys.stderr)
    print("english chars   : %d" % total_chars, file=sys.stderr)
    print("keys w/ placeholder: %d" % with_ph, file=sys.stderr)
    print("keys w/ newline : %d" % with_nl, file=sys.stderr)
    longest = sorted(out, key=lambda r: -len(r["text"]))[:3]
    for r in longest:
        print("longest %d chars [%s] %s" % (len(r["text"]), r["slot"], r["key"]), file=sys.stderr)


if __name__ == "__main__":
    main()
