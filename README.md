# erstesJava
Experimentieren mit Java und JFrame


Ich gebe dir nun ein Java Projekt. Überschriften sind, in welchen Packages sie sich befinden. Das Projekt ist ein Chat in Java.
In dem einen Paket sind Serverklassen. Hier sind die Authentifizierungsklasse, die Server Klasse an sich.
In dem anderen Paket sind Client Klassen. Hier ist der ChatClient und dazugehörig die Klassen die die Fenster erstellen sollen und die die Schnittstelle zu der
Authentifizierung im Server sind.
Es soll beim Login die Login daten dem Server über die Sockets gesendet werden, und dort durch die jeweiligen Klassen gecheckt werden, 
ob die Login Daten gültig sind, der User sich dementsprechend registriert hat oder ein falsches Passwort eingegeben hat usw.
Für diese Ereignisse wird dann über die Sockets zum Client zurückgeschickt, was der Fall ist und dann soll dieser Ein Fenster anzeigen mit der Jeweiligen nachricht, ob der login fehlgeschlagen ist oder nicht. Bei Erfolg soll das ChatFenster geöffnet werden.
Hier wird dann den ganzen Clients angezeigt, dass sich ein user connected hat.
Außerdem soll auf disconnect eines clients der Server trotzdem weiterlaufen und andere Clients sollen natürlich auch reinschreiben können.
Die Fenster sollen mit der JFrame library gemacht werden. Zusätzlich sollen im Login Fenster folgende Eingabemöglichkeiten da sein:
Username, Passwort, Port, IP, und drei Buttons. Der eine soll dafür da sein die Hintergrundfarbe des Chats auszuwählen. Die zwei anderen sind für Register und Connect.
Bei Register wird ein neuer User angelegt bzw. auch geprüft ob der Username schon vorhanden ist. Bei Connect soll Serverseitig abgeglichen werden, ob das Passwort stimmt und wenn nicht eine Meldung geschehen, dass dieser User nicht existiert oder dass man das falsche Passwort angegeben hat.
Wenn man in Port und IP nichts eingegeben hat. Soll eine Default-IP "localhost" und ein Default-Port "0000" für den Server genutzt werden.
Wenn man beim Background auswählen keine Farbe auswählt, soll standardmäßig weiß benutzt werden.

Das Chatfenster hat ein eingabefeld und ein größeres ausgabefeld. Im eingabefeld können Nachrichten eingegeben werden. Ein button daneben "Send" oder ein Enter sendet die Nachrichten zum Server und diese werden an die Clients dann verteilt. Im ausgabefeld werden die nachrichten die alle eingeben und die Join und Disconnect Messages angezeigt.
