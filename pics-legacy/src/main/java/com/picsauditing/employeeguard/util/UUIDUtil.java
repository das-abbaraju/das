package com.picsauditing.employeeguard.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;

public class UUIDUtil {

	public static String newGuid() {
		return newRandomGuid();
	}

	public static String newEthAndTimeBasedGuid(){
		EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
		TimeBasedGenerator uuid_gen = Generators.timeBasedGenerator(ethernetAddress);
		UUID uuid = uuid_gen.generate();
		return uuid.toString();
	}

	public static String newRandomGuid(){
		RandomBasedGenerator uuid_gen = Generators.randomBasedGenerator();
		UUID uuid = uuid_gen.generate();
		return uuid.randomUUID().toString();
	}

}
