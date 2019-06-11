package com.common.queue.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

public class KafkaMessageSerializer implements Serializer {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Map arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] serialize(String arg0, Object arg1) {
		if (arg1 instanceof KafkaMessageManager) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(arg1);
				out.flush();
				byte[] bytes = bos.toByteArray();
				return bytes;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException ex) {
					// ignore close exception
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

}
