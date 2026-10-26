package app.fieldwatch.domain

/**
 * English sentences compiled into the APK. A language file can replace a key.
 * It cannot add one. Official sentences are not keys.
 */
enum class LanguageSlot { TAB, CLASSCHIP, CONTROL, BODY }

data class EnglishLine(
    val key: String,
    val text: String,
    val slot: LanguageSlot,
    /** versionName that added this key. The first map shares one version. */
    val addedIn: String,
)

data class BundledLanguage(val tag: String, val name: String, val english: String)

object BundledLanguages {
    val all: List<BundledLanguage> = listOf(
        BundledLanguage("sq", "Shqip", "Albanian"),
        BundledLanguage("bg", "Български", "Bulgarian"),
        BundledLanguage("hr", "Hrvatski", "Croatian"),
        BundledLanguage("cs", "Čeština", "Czech"),
        BundledLanguage("da", "Dansk", "Danish"),
        BundledLanguage("nl", "Nederlands", "Dutch"),
        BundledLanguage("et", "Eesti", "Estonian"),
        BundledLanguage("fi", "Suomi", "Finnish"),
        BundledLanguage("fr", "Français", "French"),
        BundledLanguage("de", "Deutsch", "German"),
        BundledLanguage("el", "Ελληνικά", "Greek"),
        BundledLanguage("hu", "Magyar", "Hungarian"),
        BundledLanguage("is", "Íslenska", "Icelandic"),
        BundledLanguage("it", "Italiano", "Italian"),
        BundledLanguage("lv", "Latviešu", "Latvian"),
        BundledLanguage("lt", "Lietuvių", "Lithuanian"),
        BundledLanguage("mk", "Македонски", "Macedonian"),
        BundledLanguage("nb", "Norsk", "Norwegian"),
        BundledLanguage("pl", "Polski", "Polish"),
        BundledLanguage("pt", "Português", "Portuguese"),
        BundledLanguage("ro", "Română", "Romanian"),
        BundledLanguage("sk", "Slovenčina", "Slovak"),
        BundledLanguage("sl", "Slovenščina", "Slovenian"),
        BundledLanguage("es", "Español", "Spanish"),
        BundledLanguage("sv", "Svenska", "Swedish"),
        BundledLanguage("tr", "Türkçe", "Turkish"),
    )

    private val byTag: Map<String, BundledLanguage> = all.associateBy { it.tag }

    fun contains(tag: String): Boolean = tag in byTag

    /** Name stored in the language file. */
    fun name(tag: String): String = byTag[tag]?.name ?: tag

    /** Dropdown and Settings row. Fixed English, so the list stays in one order. */
    fun english(tag: String): String = byTag[tag]?.english ?: tag
}

/** Compiled sentences a pack cannot replace. Not keys. */
object OfficialSentences {
    const val NOT_REVIEWED =
        "Language packs are made by other people. They are not reviewed or certified by Off Grid Pete LLC."
    const val AI =
        "This language was translated with AI. If a line is wrong, please open a GitHub issue."
    const val AUTHOR_GUIDE =
        "To write a language file, export the English template and follow the author guide on GitHub."
    const val AUTHOR_GUIDE_URL =
        "https://github.com/OffGridPete/Fieldwatch/blob/main/docs/language-pack-author-guide.md"
}

/** Export of the English map. Compiled, not pack keys, so a translation cannot rename what is exported. */
object LanguageExportCopy {
    const val BUTTON = "Export English template"
    const val SHARE = "Fieldwatch English template"
    const val FAILED = "Could not export the English template"
}

/** First-run import door. English, compiled, not pack keys. */
object LanguageDoorCopy {
    const val TITLE = "Fieldwatch is in English"
    const val BODY = "You can import a language-pack file if you already have one."
    const val CONTINUE = "Continue in English"
    const val IMPORT = "Import a language file"
}

object LanguageAiCopy {
    const val OK = "OK"
}

object EnglishCopy {
    const val ADDED_IN = "1.1.27"
    const val FORMAT = "fieldwatch-language"
    const val FORMAT_VERSION = 1

    val lines: List<EnglishLine> = buildLines() + ChromeLines.all
    val byKey: Map<String, EnglishLine> = lines.associateBy { it.key }
    val placeholders: Map<String, Set<String>> = lines.associate { it.key to placeholderNames(it.text) }

    init {
        val seen = HashSet<String>(lines.size)
        val keyRe = Regex("^[a-z0-9]+(\\.[a-z0-9]+)+$")
        val tokenRe = Regex("\\{[A-Za-z][A-Za-z0-9_]*\\}")
        for (line in lines) {
            require(seen.add(line.key)) { "Duplicate language key ${line.key}" }
            require(keyRe.matches(line.key)) { "Bad language key ${line.key}" }
            require(line.addedIn == ADDED_IN)
            var i = 0
            while (i < line.text.length) {
                val brace = line.text.indexOf('{', i)
                if (brace < 0) break
                val match = tokenRe.find(line.text, brace)
                require(match != null && match.range.first == brace) {
                    "Bad placeholder in ${line.key}"
                }
                i = match.range.last + 1
            }
        }
    }

    fun text(key: String): String = byKey.getValue(key).text

    fun cap(line: EnglishLine): Int = when (line.slot) {
        LanguageSlot.TAB, LanguageSlot.CLASSCHIP -> 18
        LanguageSlot.CONTROL -> minOf(48, line.text.length * 2).coerceAtLeast(1)
        LanguageSlot.BODY -> minOf(700, maxOf(80, line.text.length * 3))
    }

    fun cap(key: String): Int = cap(byKey.getValue(key))

    fun slotName(slot: LanguageSlot): String = when (slot) {
        LanguageSlot.TAB -> "tab"
        LanguageSlot.CLASSCHIP -> "classchip"
        LanguageSlot.CONTROL -> "control"
        LanguageSlot.BODY -> "body"
    }
}

object LanguageErrors {
    const val SETTINGS = "language.err.settings"
    const val SIGNATURES = "language.err.signatures"
    const val NOT_PACK = "language.err.not"
    const val EMPTY = "language.err.empty"
    const val LARGE = "language.err.large"
    const val NONE = "language.err.none"
}

internal fun en(key: String, text: String, slot: LanguageSlot) =
    EnglishLine(key, text, slot, EnglishCopy.ADDED_IN)

private fun buildLines(): List<EnglishLine> = listOf(
    en("tab.live", "Live", LanguageSlot.TAB),
    en("tab.pause", "Pause", LanguageSlot.TAB),
    en("tab.filters", "Filters", LanguageSlot.TAB),
    en("tab.signatures", "Signatures", LanguageSlot.TAB),
    en("tab.reports", "Reports", LanguageSlot.TAB),
    en("tab.settings", "Settings", LanguageSlot.TAB),
    en("chrome.pause.display", "Pause display", LanguageSlot.CONTROL),
    en("chrome.back", "Back", LanguageSlot.CONTROL),
    en("chrome.extra.attention", "Extra attention", LanguageSlot.CONTROL),
    en("class.finder", "Finder tags", LanguageSlot.CLASSCHIP),
    en("class.beacon", "Retail beacons", LanguageSlot.CLASSCHIP),
    en("class.signage", "Signage", LanguageSlot.CLASSCHIP),
    en("class.wearable", "Wearables", LanguageSlot.CLASSCHIP),
    en("class.surveillance", "Surveillance", LanguageSlot.CLASSCHIP),
    en("class.drone", "Drones", LanguageSlot.CLASSCHIP),
    en("class.hacking", "Pentest", LanguageSlot.CLASSCHIP),
    en("class.bodyworn", "Body-worn", LanguageSlot.CLASSCHIP),
    en("class.law", "Public safety", LanguageSlot.CLASSCHIP),
    en("class.vehicle", "Vehicle", LanguageSlot.CLASSCHIP),
    en("class.glasses", "Glasses", LanguageSlot.CLASSCHIP),
    en("class.audio", "Audio", LanguageSlot.CLASSCHIP),
    en("class.camera", "Cameras", LanguageSlot.CLASSCHIP),
    en("class.thermostat", "Thermostats", LanguageSlot.CLASSCHIP),
    en("class.lock", "Access control", LanguageSlot.CLASSCHIP),
    en("class.health", "Health", LanguageSlot.CLASSCHIP),
    en("class.home", "Home IoT", LanguageSlot.CLASSCHIP),
    en("class.isp", "ISP / routers", LanguageSlot.CLASSCHIP),
    en("class.mesh", "Mesh", LanguageSlot.CLASSCHIP),
    en("class.phone", "Phones / PCs", LanguageSlot.CLASSCHIP),
    en("class.other", "Other", LanguageSlot.CLASSCHIP),
    en("speech.finder", "finder tags", LanguageSlot.CONTROL),
    en("speech.beacon", "retail beacons", LanguageSlot.CONTROL),
    en("speech.signage", "signage", LanguageSlot.CONTROL),
    en("speech.wearable", "wearables", LanguageSlot.CONTROL),
    en("speech.surveillance", "surveillance", LanguageSlot.CONTROL),
    en("speech.drone", "drones", LanguageSlot.CONTROL),
    en("speech.hacking", "pentest", LanguageSlot.CONTROL),
    en("speech.bodyworn", "body worn", LanguageSlot.CONTROL),
    en("speech.law", "public safety", LanguageSlot.CONTROL),
    en("speech.vehicle", "vehicle", LanguageSlot.CONTROL),
    en("speech.glasses", "glasses", LanguageSlot.CONTROL),
    en("speech.audio", "audio", LanguageSlot.CONTROL),
    en("speech.camera", "cameras", LanguageSlot.CONTROL),
    en("speech.thermostat", "thermostats", LanguageSlot.CONTROL),
    en("speech.lock", "access control", LanguageSlot.CONTROL),
    en("speech.health", "health", LanguageSlot.CONTROL),
    en("speech.home", "home I O T", LanguageSlot.CONTROL),
    en("speech.isp", "I S P routers", LanguageSlot.CONTROL),
    en("speech.mesh", "mesh", LanguageSlot.CONTROL),
    en("speech.phone", "phones", LanguageSlot.CONTROL),
    en("speech.other", "other", LanguageSlot.CONTROL),
    en("settings.topic.appearance", "Appearance", LanguageSlot.CONTROL),
    en("settings.topic.scanning", "Scanning", LanguageSlot.CONTROL),
    en("settings.topic.watchlist", "Watchlist", LanguageSlot.CONTROL),
    en("settings.topic.location", "Location", LanguageSlot.CONTROL),
    en("settings.topic.location.maps", "Location and Maps", LanguageSlot.CONTROL),
    en("settings.topic.gnss", "GNSS interference", LanguageSlot.CONTROL),
    en("settings.topic.tak", "TAK / CoT", LanguageSlot.CONTROL),
    en("settings.topic.logging", "Logging", LanguageSlot.CONTROL),
    en("settings.topic.signatures", "Signatures", LanguageSlot.CONTROL),
    en("settings.topic.backup", "Settings backup", LanguageSlot.CONTROL),
    en("settings.topic.language", "Language", LanguageSlot.CONTROL),
    en("settings.topic.maps", "Maps", LanguageSlot.CONTROL),
    en("settings.map.choice", "Map", LanguageSlot.CONTROL),
    en("map.openstreetmap", "OpenStreetMap", LanguageSlot.CONTROL),
    en("map.usgs.imagery", "USGS Imagery", LanguageSlot.CONTROL),
    en("map.usgs.topo", "USGS Topo", LanguageSlot.CONTROL),
    en("map.esri.satellite", "Esri Satellite", LanguageSlot.CONTROL),
    en("map.esri.streets", "Esri Streets", LanguageSlot.CONTROL),
    en("map.esri.outdoor", "Esri Outdoor", LanguageSlot.CONTROL),
    en("map.maptiler.satellite", "MapTiler Satellite", LanguageSlot.CONTROL),
    en("map.maptiler.streets", "MapTiler Streets", LanguageSlot.CONTROL),
    en("map.maptiler.outdoor", "MapTiler Outdoor", LanguageSlot.CONTROL),
    en("map.stadia.satellite", "Stadia Satellite", LanguageSlot.CONTROL),
    en("map.stadia.streets", "Stadia Streets", LanguageSlot.CONTROL),
    en("map.stadia.terrain", "Stadia Terrain", LanguageSlot.CONTROL),
    en("map.thunderforest.streets", "Thunderforest Streets", LanguageSlot.CONTROL),
    en("map.thunderforest.outdoors", "Thunderforest Outdoors", LanguageSlot.CONTROL),
    en("map.thunderforest.transport", "Thunderforest Transport", LanguageSlot.CONTROL),
    en("map.key.mark", "Key", LanguageSlot.BODY),
    en("map.key.label", "Map key", LanguageSlot.BODY),
    en("map.key.hint", "Paste the key from your account. It stays on this phone and is left out of Export settings.", LanguageSlot.BODY),
    en("map.key.missing.title", "This map needs a key", LanguageSlot.BODY),
    en("map.key.missing.body", "Paste the key from your account. Fieldwatch keeps it on this phone.", LanguageSlot.BODY),
    en("map.key.denied.title", "Key not saved", LanguageSlot.BODY),
    en("map.key.denied.body", "Fieldwatch did not store that. Use the key from your account. Only letters, numbers, and . - _ are kept.", LanguageSlot.BODY),
    en("map.key.rejected.title", "Map key was rejected", LanguageSlot.BODY),
    en("map.key.rejected.body", "The map service did not accept this key. Check the key and that the account allows this app.", LanguageSlot.BODY),
    en("map.key.failed.title", "Map did not load", LanguageSlot.BODY),
    en("map.key.failed.body", "Check the key and that this phone can reach the internet.", LanguageSlot.BODY),
    en("settings.map.about", "Show live path, Reports → Path, and the path figure use this map. Online place names and maps has to be on. OpenStreetMap covers the world. USGS Imagery and USGS Topo cover the United States. A map marked Key covers the world and uses a key from that service. The key stays on this phone. The small line in the corner names the map.", LanguageSlot.BODY),
    en("settings.map.needs.online", "Turn on Online place names and maps to load a map.", LanguageSlot.BODY),
    en("settings.map.position", "Position", LanguageSlot.CONTROL),
    en("map.position.gps", "GPS", LanguageSlot.CONTROL),
    en(
        "settings.map.position.about",
        "Where this phone is now, on the lower left of the map.",
        LanguageSlot.BODY,
    ),
    en("settings.named.radios", "Named radios", LanguageSlot.CONTROL),
    en("language.follow", "Use the phone's locale if available", LanguageSlot.CONTROL),
    en("language.english", "English", LanguageSlot.CONTROL),
    en("language.import", "Import language pack", LanguageSlot.CONTROL),
    en("language.remove", "Remove language pack", LanguageSlot.CONTROL),
    en("language.section.set", "Set", LanguageSlot.CONTROL),
    en(
        "language.section.set.about",
        "Select an available language.",
        LanguageSlot.BODY,
    ),
    en("language.section.add", "Add", LanguageSlot.CONTROL),
    en(
        "language.section.add.about",
        "Import a language file that does not come with the app. Fieldwatch does not download one.",
        LanguageSlot.BODY,
    ),
    en("language.section.remove", "Remove", LanguageSlot.CONTROL),
    en(
        "language.section.remove.about",
        "Remove the imported file on this phone. Fieldwatch then follows the phone language.",
        LanguageSlot.BODY,
    ),
    en(
        "language.import.blocks",
        "Remove the imported pack to choose a different language.",
        LanguageSlot.BODY,
    ),
    en("language.remove.title", "Remove this language pack?", LanguageSlot.BODY),
    en(
        "language.remove.body",
        "This deletes the imported file on this phone.\n\nFieldwatch then follows the phone language.",
        LanguageSlot.BODY,
    ),
    en("language.import.action", "Import", LanguageSlot.CONTROL),
    en("language.cancel", "Cancel", LanguageSlot.CONTROL),
    en("language.use", "Use", LanguageSlot.CONTROL),
    en("language.confirm.use", "Use {language}?", LanguageSlot.BODY),
    en("live.show.all", "Show all", LanguageSlot.CONTROL),
    en("live.collapse.empty", "Collapse empty", LanguageSlot.CONTROL),
    en("live.summary.radios", "{n} radios", LanguageSlot.CONTROL),
    en("live.summary.unmatched", "{n} unmatched", LanguageSlot.CONTROL),
    en("live.summary.multi", "{n} in more than one class", LanguageSlot.CONTROL),
    en("live.summary.hidden", "{n} empty hidden", LanguageSlot.CONTROL),
    en("live.no.signature", "no signature", LanguageSlot.CONTROL),
    en("live.signatures", "{n} signatures", LanguageSlot.CONTROL),
    en("live.flood.hiding", "Hiding {n} flood radios", LanguageSlot.CONTROL),
    en("live.new.hidden", "New only · {n} hidden", LanguageSlot.CONTROL),
    en("live.follow.path", "Follow · path {n} m · Start over clears the path", LanguageSlot.CONTROL),
    en("live.path.short", "GPS path {n} m — too short. Keep moving. If this stays 0, Location is not updating (use high accuracy).", LanguageSlot.BODY),
    en("live.already.hidden", "{n} already seen are hidden. A new radio stays while we hear it, then at least as long as Brief hold after the last packet.", LanguageSlot.BODY),
    en("live.gnss.paused", "GNSS paused by the phone", LanguageSlot.CONTROL),
    en("ui.n.gone", "{n} gone", LanguageSlot.CONTROL),
    en("ui.feed.on", "on the feed {n}", LanguageSlot.CONTROL),
    en("ui.feed.sent", "sent {n}", LanguageSlot.CONTROL),
    en("ui.feed.status", "Feed status", LanguageSlot.CONTROL),
    en("ui.pick.second.sit", "Pick a second sit.", LanguageSlot.CONTROL),
    en("ui.could.not.read.sit", "Could not read that sit.", LanguageSlot.CONTROL),
    en("ui.could.not.read.this.sit", "Could not read this sit.", LanguageSlot.CONTROL),
    en("ui.could.not.write.location", "Could not write to the location you picked.", LanguageSlot.CONTROL),
    en("ui.could.not.read.file", "Could not read that file.", LanguageSlot.CONTROL),
    en("ui.format.sit.export", "That format is a sit export.", LanguageSlot.CONTROL),
    en("ui.could.not.open.location", "Could not open the selected location", LanguageSlot.CONTROL),
    en("ui.pick.map.format", "Pick a map format.", LanguageSlot.CONTROL),
    en("ui.no.gps.tagged.radios", "No GPS-tagged radios. Settings → Tag detections with GPS, logging on, then sit.", LanguageSlot.BODY),
    en("ui.no.gps.tagged.wifi", "No GPS-tagged Wi-Fi radios. Settings → Tag detections with GPS, logging on, then sit.", LanguageSlot.BODY),
    en("ui.no.gps.tagged.ble", "No GPS-tagged BLE radios. Settings → Tag detections with GPS, logging on, then sit.", LanguageSlot.BODY),
    en("ui.writing.what", "Writing {what}", LanguageSlot.CONTROL),
    en("ui.writing.radios", "Writing {what} · {n} radios", LanguageSlot.CONTROL),
    en("ui.writing.pins", "Writing {what} · {n} pins", LanguageSlot.CONTROL),
    en("ui.writing.progress", "Writing {what} · {done} of {total}", LanguageSlot.CONTROL),
    en("ui.reading.kb", "Reading log · {done} KB of {total} KB", LanguageSlot.CONTROL),
    en("ui.writing.kb", "Writing {done} KB of {total} KB", LanguageSlot.CONTROL),
    en("diag.on", "on", LanguageSlot.CONTROL),
    en("diag.off", "off", LanguageSlot.CONTROL),
    en("diag.granted", "granted", LanguageSlot.CONTROL),
    en("diag.missing", "missing", LanguageSlot.CONTROL),
    en("diag.parked", "parked", LanguageSlot.CONTROL),
    en("diag.retrying", "retrying", LanguageSlot.CONTROL),
    en("diag.starting", "starting", LanguageSlot.CONTROL),
    en("diag.running", "running", LanguageSlot.CONTROL),
    en("diag.none", "none", LanguageSlot.CONTROL),
    en("diag.waiting.age", "waiting on OS ({age})", LanguageSlot.CONTROL),
    en("diag.seconds.ago", "{n}s ago", LanguageSlot.CONTROL),
    en("diag.minutes.ago", "{n} min ago", LanguageSlot.CONTROL),
    en("diag.filter.hiding", "A filter is hiding radios. Filters is {filters}. Set Filters to All Traffic to see everything the phone hears.", LanguageSlot.BODY),
    en("language.confirm.title", "Import {language}?", LanguageSlot.BODY),
    en(
        "language.confirm.written",
        "Written for {version}. This phone is {phone}.",
        LanguageSlot.BODY,
    ),
    en("language.confirm.change", "{n} lines will change.", LanguageSlot.BODY),
    en("language.confirm.stay", "{n} lines will stay English.", LanguageSlot.BODY),
    en("language.confirm.ignored", "{n} lines in the file were ignored.", LanguageSlot.BODY),
    en("language.confirm.new", "{n} keys are new since your pack.", LanguageSlot.BODY),
    en(
        "language.confirm.newer",
        "This pack uses a newer format. Only the lines were used.",
        LanguageSlot.BODY,
    ),
    en(
        "language.err.settings",
        "That is a settings pack. Use Import settings.",
        LanguageSlot.BODY,
    ),
    en(
        "language.err.signatures",
        "That is a signature pack. Use Import signatures.",
        LanguageSlot.BODY,
    ),
    en("language.err.not", "Not a Fieldwatch language pack.", LanguageSlot.BODY),
    en("language.err.empty", "This file is empty.", LanguageSlot.BODY),
    en("language.err.large", "That file is too large.", LanguageSlot.BODY),
    en("language.err.none", "That file has no lines this version can use.", LanguageSlot.BODY),
    en("header.wifi.off", "Wi-Fi off", LanguageSlot.CONTROL),
    en("header.wifi.idle", "Wi-Fi idle", LanguageSlot.CONTROL),
    en("header.wifi.location", "Wi-Fi, Location is off", LanguageSlot.CONTROL),
    en("header.wifi.wait", "Wi-Fi waiting on Android", LanguageSlot.CONTROL),
    en("header.wifi.on", "Wi-Fi on", LanguageSlot.CONTROL),
    en("header.ble.off", "Bluetooth off", LanguageSlot.CONTROL),
    en("header.ble.idle", "Bluetooth idle", LanguageSlot.CONTROL),
    en("header.ble.location", "Bluetooth, Location is off", LanguageSlot.CONTROL),
    en("header.ble.wait", "Bluetooth waiting on Android", LanguageSlot.CONTROL),
    en("header.ble.on", "Bluetooth on", LanguageSlot.CONTROL),
    en("header.gps.alert", "GPS alert", LanguageSlot.CONTROL),
    en("header.gps.paused", "GPS paused", LanguageSlot.CONTROL),
    en("header.gps.on", "GPS on", LanguageSlot.CONTROL),
    en("export.log.csv", "Log file — CSV", LanguageSlot.CONTROL),
    en("export.log.jsonl", "Log file — JSON lines", LanguageSlot.CONTROL),
    en("export.gpx", "GPX — GPS Exchange", LanguageSlot.CONTROL),
    en("export.kml", "KML — Google Earth", LanguageSlot.CONTROL),
    en("export.geojson", "GeoJSON — map file", LanguageSlot.CONTROL),
    en("export.wigle", "WiGLE CSV — wigle.net", LanguageSlot.CONTROL),
    en("export.atak", "ATAK data package", LanguageSlot.CONTROL),
    en("export.both", "Both radios", LanguageSlot.CONTROL),
    en("export.wifi.only", "Wi-Fi only", LanguageSlot.CONTROL),
    en("export.ble.only", "BLE only", LanguageSlot.CONTROL),
    en("reports.debrief.pdf", "Debrief (PDF)", LanguageSlot.CONTROL),
    en("reports.compare.pdf", "Compare (PDF)", LanguageSlot.CONTROL),
    en("reports.path", "Path", LanguageSlot.CONTROL),
    en("reports.debrief", "Debrief", LanguageSlot.CONTROL),
    en("reports.sit.export", "Sit export", LanguageSlot.CONTROL),
    en("reports.compare", "Compare", LanguageSlot.CONTROL),
    en("reports.log.export", "Log Export", LanguageSlot.CONTROL),
    en("reports.candidates", "Signature candidates", LanguageSlot.CONTROL),
    en("reports.sits", "Sits", LanguageSlot.CONTROL),
    en("rule.oui", "OUI", LanguageSlot.CONTROL),
    en("rule.mac.prefix", "MAC prefix", LanguageSlot.CONTROL),
    en("rule.hidden.ssid", "Matches hidden SSIDs", LanguageSlot.CONTROL),
    en("rule.hidden.label", "Hidden SSID", LanguageSlot.CONTROL),
    en("display.rssi", "RSSI bars", LanguageSlot.CONTROL),
    en("display.live.path", "Show live path", LanguageSlot.CONTROL),
    en("display.recenter", "Recenter", LanguageSlot.CONTROL),
    en("map.north.up", "North up", LanguageSlot.BODY),
    en("map.rotate.travel", "Rotate with travel", LanguageSlot.BODY),
    en("map.close.heading", "Nearby, heading up", LanguageSlot.BODY),
    en("map.close.north", "Nearby, north up", LanguageSlot.BODY),
    en("ui.radios.here", "Radios here: {n}", LanguageSlot.CONTROL),
    en("ui.move.path", "Move the path", LanguageSlot.CONTROL),
    en("ui.waiting.gps.fix", "Waiting for a GPS fix.", LanguageSlot.BODY),
    en("display.mac", "MAC address", LanguageSlot.CONTROL),
    en("display.advertised", "Advertised name", LanguageSlot.CONTROL),
    en("display.name.type", "Name + type", LanguageSlot.CONTROL),
    en("display.none", "None", LanguageSlot.CONTROL),
    en("view.radar", "Classic radar", LanguageSlot.CONTROL),
    en("view.list", "Strength list", LanguageSlot.CONTROL),
    en("view.timeline", "Timeline", LanguageSlot.CONTROL),
    en("view.hybrid", "Hybrid + sparklines", LanguageSlot.CONTROL),
    en("view.by.class", "By class", LanguageSlot.CONTROL),
    en("voice.class", "Class", LanguageSlot.CONTROL),
    en("voice.signature", "Signature", LanguageSlot.CONTROL),
    en("voice.both", "Class + signature", LanguageSlot.CONTROL),
    en("sort.name", "Name A–Z", LanguageSlot.CONTROL),
    en("sort.class", "Class A–Z", LanguageSlot.CONTROL),
    en("radio.wifi", "Wi-Fi", LanguageSlot.CONTROL),
    en("radio.ble", "BLE", LanguageSlot.CONTROL),
)
