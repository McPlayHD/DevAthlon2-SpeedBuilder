# SpeedBuilder
Minigame for DefAthlon 2

Ich habe das Plugin mit der spigot version 1.7.10 mit dem 1.8 Script gecodet (werde den Link noch einfügen).
Jedoch sollte es für einen 1.7.10 sowie auch einen 1.8 Server funktionieren.

Hier ist das kleine Tutorial, wie man das Plugin installiert.


Als erstens muss man einfach die jar in den plugins ordner stellen.

Des weiteren ladet ihr am besten diese Map hier runter und legt sie in den Hauptordher des Servers:

http://mcplayhd.net/downloads/SpeedBuilder.zip

Danach startet ihr den Server und importiert die Map mit Multiverse-Core

WICHTIG!!! Diese Map darf nicht die Main-map sein, da sie nach jedem Neustart wieder neu eingefügt wird.

Sobald ihr dies getan habt, stoppt ihr den Server nochmals und geht in die erstellte config.yml in /plugins/SpeedBuilder

Dort fügt ihr die Informationen zur MySQl Datenbank ein. Die Tabelle wird automatisch erstellt, sobald das Plugin einsatzbereit ist.

Danach startet ihr den Server wieder, geht auf die Map und sucht euch einen passenden Spawnpunkt aus.

WICHTIG ihr müsst Operator sein, um das Plugin aufzusetzen

Sobald ihr an der gewünschten Position steht, geht ihr folgende Commands ein:

/speedbuilder spawn

/speedbuilder world

Danach geht ihr zur Top-5 Wand und schaut den ersten Kopf an. Dann gebt ihr folgenden Command ein:

/speedbuilder top head 1

Diesen Command wiederholt ihr für die anderen 4 Köpfe, nur dass ihr dort den Wert ändert.

Dann müssen nur noch die Schilder gesetzt werden.

Dazu schaut ihr das erste Schild an und führt diesen Command aus:

/speedbuilder top sign 1

Auch diesen Command wiederholt ihr für die anderen Schilder.

Alle Commands werden auch abgebildet, wenn ihr /help ausführt.

Danach muss der Server noch ein Mal neugestartet werden.

Das Plugin sollte nun funktionieren. Falls dies nicht der Fall ist und ihr sicher seid, dass ihr alles richtig aufgesetzt habt, geht in die config.yml und setzt den Boolean "Complete" auf true und restartet den Server.

Das Spiel funktioniert folgendermassen:

Die Spieler müssen sich nacheinander von der Spawninsel zum Ziel bauen. Und dies so schnell wie möglich.

Alle anderen Spieler, welche online sind, sind Spectator und liegen in einer Warteschlaufe.

Sinn des Spieles: Die EagleTaktik lernen. (Fragt dazu _BlackEagle_ um nähere Erklärungen)

Die schnellsten 5 Spieler werden auf dier Top-5 Wand vermerkt.


Hier noch die Links:

Welt: http://mcplayhd.net/downloads/SpeedBuilder.zip

Spigot: http://mcplayhd.net/downloads/Spigot.jar

Plugin: http://mcplayhd.net/downloads/SpeedBuilder.jar


Benötigt werden nur Multiverse-Core und eine MySQl Datenbank.

Empfohlen wird NoCheatPlus, da ich in dieser Zeit nichts gegen Flyhacker geschrieben habe.
