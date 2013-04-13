## Naogateway

Make a naoqi API available in scala with akka, protobuf and zeromq under the hood

### Installationsanleitung 

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
- - 	hosted only und erstellten Netzwerk (Standardnetzwerkkarte) -> ssh Zugriff
- - 	NAT -> Internet
- in /etc/network/interfaces zweites Interface eintragen
-  auto eth1
-  iface eth1 inet dhcp
- sudo apt-get install libzmq1 libzmq-dev libprotobuf-java git
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
autotools-dev (20120608.1)
binutils (2.22.90.20120924-0ubuntu2)
cpp-4.7 (4.7.2-2ubuntu1)
cpp (4:4.7.2-1ubuntu2)
gcc-4.7 (4.7.2-2ubuntu1)
gcc (4:4.7.2-1ubuntu2)
pkg-config (0.26-1ubuntu2)
libtool (2.4.2-1ubuntu2)
autoconf (2.69-1ubuntu1)
automake (1:1.11.6-1ubuntu1)
g++ (4:4.7.2-1ubuntu2)
make (3.81-8.2ubuntu2)

./autogen.sh

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

sudo update-alternatives --install "/usr/bin/java" "java" "/opt/Oracle_Java/jdk1.6.0_38/bin/java" 1
sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/Oracle_Java/jdk1.6.0_38/bin/javac" 1
sudo update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/Oracle_Java/jdk1.6.0_38/bin/javaws" 1
sudo update-alternatives --install "/usr/bin/jar" "jar" "/opt/Oracle_Java/jdk1.6.0_38/bin/jar" 1 
sudo update-alternatives --install "/usr/bin/javah" "javah" "/opt/Oracle_Java/jdk1.6.0_38/bin/javah" 1

sudo update-alternatives --set "java" "/opt/Oracle_Java/jdk1.6.0_38/bin/java"
sudo update-alternatives --set "javac" "/opt/Oracle_Java/jdk1.6.0_38/bin/javac"
sudo update-alternatives --set "javaws" "/opt/Oracle_Java/jdk1.6.0_38/bin/javaws"
sudo update-alternatives --set "jar" "/opt/Oracle_Java/jdk1.6.0_38/bin/jar" 
sudo update-alternatives --set "javah" "/opt/Oracle_Java/jdk1.6.0_38/bin/javah"

### Wieder zu jzmq
./configure
make
sudo make install

### Installation Simple Build Tool
wget http://apt.typesafe.com/repo-deb-build-0002.deb
dpkg -i repo-deb-build-0002.deb
apt-get install typesafe-stack

### Installation naogateway
git clone https://github.com/hannibalhh/naogateway.git
cd naogateway
sbt run

### Konfigurationsdatei
naogateway/src/main/resources/application.conf

have fun



