# -*- coding: utf-8 -*-
"""
Validate a Fieldwatch zh language pack against the compiled English map,
reproducing LanguagePack.acceptLines() + EnglishCopy.cap() from app source.

Usage: python validate_zh.py            (builds zh.json from tr*.py, then validates)
"""
import glob
import importlib.util
import json
import os
import re
import sys

BASE = os.path.dirname(os.path.abspath(__file__))
PLACE_RE = re.compile(r'\{([A-Za-z][A-Za-z0-9_]*)\}')


def placeholder_names(text):
    return set(PLACE_RE.findall(text))


def clean(raw, allow_newline):
    """Mirror of LanguagePack.clean()."""
    out = []
    for ch in raw:
        if ch == '\r':
            continue
        if ch == '\n':
            if allow_newline:
                out.append('\n')
            continue
        if ch == '\uFEFF' or ch in '\u200E\u200F':
            continue
        if '\u202A' <= ch <= '\u202E' or '\u2066' <= ch <= '\u2069':
            continue
        if ord(ch) < 0x20 or ord(ch) == 0x7F:
            continue
        import unicodedata
        cat = unicodedata.category(ch)
        if cat in ('Cc', 'Cf'):
            continue
        out.append(ch)
    return ''.join(out)


def load_en():
    rows = json.load(open(os.path.join(BASE, "en_map.json"), encoding="utf-8"))
    return {r["key"]: r for r in rows}


def load_translations():
    """Merge every tr*.py T dict, in filename order."""
    merged = {}
    dups = []
    for path in sorted(glob.glob(os.path.join(BASE, "tr*.py"))):
        spec = importlib.util.spec_from_file_location("m_" + os.path.basename(path)[:-3], path)
        mod = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(mod)
        for k, v in mod.T.items():
            if k in merged:
                dups.append((k, os.path.basename(path)))
            merged[k] = v
    if dups:
        print("!! duplicate keys across batches:", file=sys.stderr)
        for k, f in dups:
            print("   %s in %s" % (k, f), file=sys.stderr)
    return merged


def main():
    en = load_en()
    tr = load_translations()
    print("english keys : %d" % len(en), file=sys.stderr)
    print("translated   : %d" % len(tr), file=sys.stderr)

    unknown = [k for k in tr if k not in en]
    if unknown:
        print("!! %d keys NOT in english map (would be ignored):" % len(unknown), file=sys.stderr)
        for k in sorted(unknown):
            print("   %s" % k, file=sys.stderr)

    accepted = {}
    dropped = []
    for k, v in tr.items():
        line = en.get(k)
        if line is None:
            continue
        nl_ok = '\n' in line["text"]
        if ('\n' in v or '\r' in v) and not nl_ok:
            dropped.append((k, "newline but english has none", v))
            continue
        c = clean(v, nl_ok).strip()
        if not c:
            dropped.append((k, "empty after clean", v))
            continue
        if len(c) > line["cap"]:
            dropped.append((k, "too long %d > cap %d" % (len(c), line["cap"]), v))
            continue
        if placeholder_names(c) != placeholder_names(line["text"]):
            dropped.append((k, "placeholder mismatch %s vs %s" % (
                sorted(placeholder_names(c)), sorted(placeholder_names(line["text"]))), v))
            continue
        accepted[k] = c

    print("accepted     : %d" % len(accepted), file=sys.stderr)
    print("dropped      : %d" % len(dropped), file=sys.stderr)
    for k, why, v in dropped:
        print("   DROP %-40s %s | %r" % (k, why, v[:80]), file=sys.stderr)

    missing = [k for k in en if k not in accepted]
    print("stay english : %d" % len(missing), file=sys.stderr)

    pack = {
        "format": "fieldwatch-language",
        "formatVersion": 1,
        "language": "简体中文",
        "languageTag": "zh",
        "writtenFor": "1.1.31",
        "strings": {k: accepted[k] for k in en if k in accepted},  # english-map order
    }
    body = json.dumps(pack, ensure_ascii=False, indent=2)
    n = len(body.encode("utf-8"))
    print("file bytes   : %d (cap %d)" % (n, 256 * 1024), file=sys.stderr)
    if n > 256 * 1024:
        print("!! FILE TOO LARGE", file=sys.stderr)
    if len(pack["strings"]) > 2000:
        print("!! TOO MANY KEYS", file=sys.stderr)
    if len(clean(pack["language"], False).strip()) > 40:
        print("!! LANGUAGE NAME TOO LONG", file=sys.stderr)

    out = os.path.join(BASE, "zh.json")
    with open(out, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(body + "\n")
    print("wrote %s" % out, file=sys.stderr)
    print("coverage     : %.1f%%" % (100.0 * len(accepted) / len(en)), file=sys.stderr)

    if missing:
        with open(os.path.join(BASE, "missing.txt"), "w", encoding="utf-8") as fh:
            for k in missing:
                fh.write("%s|%s|%d|%s\n" % (k, en[k]["slot"], en[k]["cap"], en[k]["text"]))
        print("missing list -> missing.txt (%d)" % len(missing), file=sys.stderr)

    return 1 if dropped or unknown else 0


if __name__ == "__main__":
    sys.exit(main())
