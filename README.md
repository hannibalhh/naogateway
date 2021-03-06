## Naogateway

Make a naoqi API available in scala with akka, protobuf and zeromq under the hood

### Überblick

Das Aktorensystem naogateway ist ein Kommunikationsmedium zum
Kommunizieren mit dem Nao. Wer ein vorhandenes naogateway Aktorensystem
ansprechen möchte, sollte einen Blick in das Repository
https://github.com/hannibalhh/naogatewayRemoteClient werfen.
Das Scaladoc ist unter
https://github.com/hannibalhh/naogateway/tree/master/doc zu finden.

### Installationsanleitung für eine Ubuntu VM

- VirtualBox 4.2
- Neue Maschine -> Linux->Ubuntu 12.10 64Bit
- 8GB Fesplatte (dynamisch), 1024MB RAM -> Ubuntu Server 12.10 64Bit installiert (3.5er Kernel)
- Computername: naogateway
- Account: nao
- Passwort: gateway
- Aktualisierungen manuell
- Sprache und Tastaturlayot: Deutsch
- Partitionierung: Eine ext4 + swap (Variante: vollständige Festplatte verwenden)
- VirtualBox -> Einstellungen -> Netzwerk -> Netzwerk erstellen
- Open SSH Server bei Installation mitinstalliert und mit sudo start ssh gestartet
- Grub Bootloader -> MBR
- VM mit folgenden Netzwerkeinstellungen versorgen:
	-  	hosted only und erstellten Netzwerk (Standardnetzwerkkarte) -> ssh Zugriff
	- 	NAT -> Internet
- in /etc/network/interfaces zweites Interface eintragen

```	
	auto eth1
	iface eth1 inet dhcp
```

Nun kann via ssh die Installation der ersten Bibliotheken geschehen:
```
	sudo apt-get update
	sudo apt-get install libzmq1 libzmq-dev libprotobuf-java git
```

- Dabei wurden folgende Pakete installiert:
	- libpgm-5.1-0:amd64 (5.1.118-1~dfsg-0.1ubuntu1)
	- libzmq1:amd64 (2.2.0+dfsg-2ubuntu1)
	- liberror-perl (0.17-1)
	- git-man (1:1.7.10.4-1ubuntu1)
	- git (1:1.7.10.4-1ubuntu1)
	- libprotobuf-java (2.4.1-3ubuntu1)
	- libprotobuf7 (2.4.1-3ubuntu1)
	- libprotoc7 (2.4.1-3ubuntu1)
	- protobuf-compiler (2.4.1-3ubuntu1)

### Installation jzmq

	https://github.com/zeromq/jzmq Revision 6f28d8c
	sudo apt-get install pkg-config libtool autoconf automake g++ make

Dabei wurden folgende Pakete installiert (u.a.)
- autotools-dev (20120608.1)
- binutils (2.22.90.20120924-0ubuntu2)
- cpp-4.7 (4.7.2-2ubuntu1)
- cpp (4:4.7.2-1ubuntu2)
- gcc-4.7 (4.7.2-2ubuntu1)
- gcc (4:4.7.2-1ubuntu2)
- pkg-config (0.26-1ubuntu2)
- libtool (2.4.2-1ubuntu2)
- autoconf (2.69-1ubuntu1)
- automake (1:1.11.6-1ubuntu1)
- g++ (4:4.7.2-1ubuntu2)
- make (3.81-8.2ubuntu2)

```
	./autogen.sh
```

Da Ubuntu auf OpenJDK setzt und dieses eine andere Serialisierung verwendet, 
muss Oracle Java manuell installiert werden.
http://wiki.ubuntuusers.de/Java/Installation/Oracle_Java?redirect=no#Java-6-JRE

	sudo mkdir -p /opt/Oracle_Java 
	cd /opt/Oracle_Java 

Binärdateien sind zu bekommen unter:

	http://jdk6.java.net/download.html
	wget http://www.java.net/download/jdk6/6u38/promoted/b04/binaries/jdk-6u38-ea-bin-b04-linux-amd64-31_oct_2012.bin
	chmod +x jdk*.bin
	./jdk*.bin
	sudo cp -a jdk1.6.0_38/ /opt/Oracle_Java/
	sudo chown -R root:root /opt/Oracle_Java/* 

#### Java Alternative installieren

	sudo update-alternatives --install "/usr/bin/java" "java" "/opt/Oracle_Java/jdk1.6.0_38/bin/java" 1
	sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/Oracle_Java/jdk1.6.0_38/bin/javac" 1
	sudo update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/Oracle_Java/jdk1.6.0_38/bin/javaws" 1
	sudo update-alternatives --install "/usr/bin/jar" "jar" "/opt/Oracle_Java/jdk1.6.0_38/bin/jar" 1 
	sudo update-alternatives --install "/usr/bin/javah" "javah" "/opt/Oracle_Java/jdk1.6.0_38/bin/javah" 1

#### Java Alternative einrichten

	sudo update-alternatives --set "java" "/opt/Oracle_Java/jdk1.6.0_38/bin/java"
	sudo update-alternatives --set "javac" "/opt/Oracle_Java/jdk1.6.0_38/bin/javac"
	sudo update-alternatives --set "javaws" "/opt/Oracle_Java/jdk1.6.0_38/bin/javaws"
	sudo update-alternatives --set "jar" "/opt/Oracle_Java/jdk1.6.0_38/bin/jar" 
	sudo update-alternatives --set "javah" "/opt/Oracle_Java/jdk1.6.0_38/bin/javah"

### jzmq konfigurieren und compilieren 
``` ./configure
 make
 sudo make install 
```

### Installation Simple Build Tool

	wget http://apt.typesafe.com/repo-deb-build-0002.deb
	dpkg -i repo-deb-build-0002.deb
	apt-get install typesafe-stack 


### Installation naogateway

	git clone https://github.com/hannibalhh/naogateway.git
	cd naogateway
	sbt run
	
Über -h gelangt man zur kleinen Hilfe, die beschreibt wie man auf die verschiedenen
Naos zugreifen kann. Außerdem kann eine Konfigurationsdatei angegeben werden. Standardwerte sind durch = definiert.
 
	run -h
    Usage: naogateway 
	  [-n | --name naoname = nila] 
	  [-c | --config absolutepath = naogateway/src/main/resources/application.conf]
	  [-t | --test host = 127.0.0.1 port = 2552]
	  [-h | --help] 

Angenommen ein naogateway ist gestartet, so kann mit -t ein Say zum testen an 
den naogateway (remote) verschickt werden. Zu beachten ist, dass Client und Server
im gleichen Netz ihre Ports binden müssen.

Wichtig ist, dass in der Konfigurationsdatei für den noagateway der Namespace naogateway
definiert ist. Die Angabe des naos führt dazu, dass im Namespace naogateway der entsprechende 
Unterbaum als Wurzel der Konfiguration verwendet wird und naogateway als Fallback (Elternknoten)
dient. D.h. naospezifische Angaben werden in dem entsprechenden Namespace (z.B. nila)
geätigt, allgemeine Konfigurationen in naogateway. Folgende Konfiguration
ist die Minimalkonfiguration für die remote Kommunikation.

Der naogateway ist über 192.168.1.1:2552 erreichbar und kann die Naos hanna
und nila ansprechen. Der HAWActor wird über den Port 5555 angesprochen, der HAWCamAktor
wird dagegen über 5556 erreicht. Über den Namespace log können die Loggingausgaben
der einzelnen Aktoren konfiguriert werden.

	naogateway{
		hanna {    
		  nao.host = "192.168.1.11"
		  nao.name = "Hanna"
		}
		nila { 
		  nao.host = "192.168.1.10"
		  nao.name = "Nila"
		}
		nao.port = 5555
		nao.camport = 5556
		akka {
		  actor {
			provider = "akka.remote.RemoteActorRefProvider"
		  }	
		  remote {
			transport = "akka.remote.netty.NettyRemoteTransport"
			netty {
			  hostname = "192.168.1.1"
			  port = 2552
			}
		  }
		}
		log {
		   naoactor{
			info = true
			error = true
			wrongMessage = true
		  }
		  responseactor{
			info = true
			error = true
			wrongMessage = true
		  }
		  noresponseactor{
			info = true
			error = true
			wrongMessage = true
		  }
		  heartbeatactor{
			info = true
			error = true
			wrongMessage = true
		  }
		  visionactor{
			info = true
			error = true
			wrongMessage = true
		  }  
		}
		responseactor.delay {
			almotion.getcom = 200
		}
		heartbeatactor {
			online.delay = 1000
			maybeoffline.delay = 300
			offlinetimes = 3
			activated = false
		}
	}
	
Für responseactor und noreponseactor können Verzögerungen definiert werden.
Dazu wird die Syntax modulname.methodenname = value in ms verwendet. Diese Angabe ist
optional. Standardwert ist 0 ms. Ebenso kann der Heartbeat konfiguriert werden, wobei
dort statt der Methoden die Zustände online und maybeoffline definiert werden.

Um mit dem naogateway zu kommunzieren, muss auch eine kleine Konfiguration
vorgenommen werden. Folgedes Beispiel kann dafür übernommen werden. 
Host und Port dürfen nicht gleich dem naogateway sein, der host muss
außerdem im gleichen Netzwerk wie der naogateway erreichbar sein.
127.0.0.1 ist nur möglich und nötig, wenn sich beide auf dem gleichen Rechner 
befinden. Dieses Beispiel ist gleichzeitig die Konfiguration des Testapplikation.

	remoting{
		akka.loglevel = "DEBUG"
		akka {
		  actor {
			provider = "akka.remote.RemoteActorRefProvider"
		  }	
		  remote {
			transport = "akka.remote.netty.NettyRemoteTransport"
			netty {
			  hostname = "192.168.1.2"
			  port = 2551
			}
		  }
		}
	}

Die Architektur und Kommunikation ist in
https://github.com/hannibalhh/naogateway/blob/master/naogateway.pdf
beschrieben.

