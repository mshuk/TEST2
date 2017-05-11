#!/usr/bin/perl -w
# Filename : Server.pl

use strict;
use Socket;

# use port 7890 as default
my $port = shift || 7890;
my $proto = getprotobyname('tcp');
my $server = "localhost"; # Host IP running the server

# create a socket, make it reusable
socket(SOCKET, PF_INET, SOCK_STREAM, $proto)
	or die "Can't open socket $!\n";
setsockopt(SOCKET, SOL_SOCKET, SO_REUSEADDR, 1)
	or die "Can't set socket option to SO_REUSEADDR $!\n";

# bind to a port, then listen
bind( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
	or die "Can't bind to port $port! \n";

listen(SOCKET, 5) or die "listen: $!";
print "SERVER started on port $port\n";

#input string
print "Input string ";
my $string = <>;

# accepting a connection
my $client_addr;

# send them a message, close connection
$client_addr = accept(NEW_SOCKET, SOCKET);
my $name = gethostbyaddr($client_addr, AF_INET );
print NEW_SOCKET $string;
print "Send $string";
print "to Client\n";
print "Connection recieved from $name\n\n\n\n";
close NEW_SOCKET;


# create the socket, connect to the port
socket(SOCKET, PF_INET, SOCK_STREAM, $proto)
	or die "Can't create a socket $!\n";
connect( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
	or die "Can't connect to port $port! \n";

my $line;
my $concat;
while ($line = <SOCKET>) {
	$concat .= $line;	
}
print "Receive inverted text from Client\n";
print "$concat\n\a";