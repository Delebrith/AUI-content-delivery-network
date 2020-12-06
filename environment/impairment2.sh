#!/bin/bash
# this script enable netem queuing + rate limit
# the folloinwg modues must be installed: netem, iptables
#
#
# Note that for Tiny Core you need to install netem before using 
# COSs by the following command (you need access to the internet 
# to download the net-sched-3.0.3-tinycore.tcz file):
#
#    tce-load -wi net-sched-3.0.3-tinycore.tcz

print_instruction() {
	echo This script introduces network impairments for BE traffic
	echo
	echo USAGE:
	echo "  ./impairment add interface bandwidth [Mbps] buffer_size [pkt] delay [ms] loss [%]"
	echo "  ./impairment del interface"
	echo "  ./impairment show interface"
	echo
	echo EXAMPLES:
	echo "  ./impairment add eth1 10 100 5 0.1"
	echo "  ./impairment del eth1"
	echo "  ./impairment show eth1"
}


case $1 in
    add)
        if [ "$6" = "" ]; then
          echo "Wrong parameters, see below"
          print_instruction
          exit 1
        fi
	# sudo tc qdisc del dev $2 root

	echo
	# create queueing on interface
	sudo tc qdisc add dev $2 root handle 1: htb default 10
	sudo tc class add dev $2 parent 1: classid 1:1 htb rate $3mbit 
	sudo tc class add dev $2 parent 1:1 classid 1:10 htb rate $3mbit 
#	sudo tc class add dev $2 parent 1:1 classid 1:20 htb rate 10mbit 

	 # introduce packet inpairments for BE queue
        if [ "$6" = "0" ]; then
                sudo tc qdisc add dev $2 parent 1:10 handle 10: netem delay $(( $5 * 1000 )) limit $4
        else
                sudo tc qdisc add dev $2 parent 1:10 handle 10: netem delay $(( $5 * 1000 )) drop $6% limit $4
        fi
        sudo tc qdisc add dev $2 parent 1:10 handle 10: netem limit $4

	# create packet filter for PR traffic
#	sudo tc filter add dev $2 protocol all parent 1:0 prio 10 handle 2 fw flowid 1:10
#	sudo iptables -A OUTPUT -o $2 -j MARK --set-mark 2
#	sudo iptables -A FORWARD -o $2 -j MARK --set-mark 2
	;;
    del)
	echo
	sudo tc qdisc del dev $2 root
#	sudo iptables --flush
	;;
    
   show)
	echo
	#Show  Configuration of queues
	echo Queue configuration
	sudo tc class show dev $2
	sudo tc qdisc show dev $2

	#Show configuration of pkt filter
	echo
	echo Configuration of packet filters
	sudo tc filter show dev $2
	sudo iptables -L


	#Show traffic statistics
	echo
	echo Traffic statistics
	sudo tc -s qdisc ls dev $2
	;;
    *)
	print_instruction
	exit 1
	;;
esac