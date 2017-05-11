#!/usr/bin/perl -w
# Filename : Client.pl

use strict;
use warnings;
use Socket;

# initialize host and port
my $host = shift || 'localhost';
my $port = shift || 7890;
my $server = "localhost"; # Host IP running the server

# create the socket, connect to the port
socket(SOCKET,PF_INET,SOCK_STREAM,(getprotobyname('tcp'))[2])
	or die "Can't create a socket $!\n";
connect( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
	or die "Can't connect to port $port! \n";

my $line;
my $strconcat;
while ($line = <SOCKET>) {
	$strconcat .= $line;
}
my $strinvert;
my $strlength = length($strconcat) - 1;

#invert string
for ( my $x = $strlength; $x >= 0; $x = $x - 1 ){
	my $fragment = substr $strconcat, $x, 1;
	$strinvert .= $fragment;
}
print "Receive $strconcat";
print "from Server. Invert the received text to $strinvert. ";
print "Send it back to Server \n\n\a";

close SOCKET or die "close: $!";

# create a socket, make it reusable
socket(SOCKET,PF_INET,SOCK_STREAM,(getprotobyname('tcp'))[2])
	or die "Can't open socket $!\n";
setsockopt(SOCKET, SOL_SOCKET, SO_REUSEADDR, 1)
	or die "Can't set socket option to SO_REUSEADDR $!\n";

# bind to a port, then listen
bind( SOCKET, pack_sockaddr_in($port, inet_aton($server)))
	or die "Can't bind to port $port! \n";

listen(SOCKET, 5) or die "listen: $!";
print "SERVER started on port $port\n";

# accepting a connection
my $client_addr;

# send them a message, close connection
$client_addr = accept(NEW_SOCKET, SOCKET);
my $name = gethostbyaddr($client_addr, AF_INET );
print NEW_SOCKET $strinvert;
print "Connection recieved from $name\n";
close NEW_SOCKET;

close SOCKET or die "close: $!";
