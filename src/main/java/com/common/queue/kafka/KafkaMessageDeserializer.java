package com.common.queue.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

public class KafkaMessageDeserializer implements Deserializer {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Map arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public KafkaMessageManager deserialize(String arg0, byte[] arg1) {
		ByteArrayInputStream bis = new ByteArrayInputStream(arg1);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			KafkaMessageManager o = (KafkaMessageManager) in.readObject();
			return o;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

}
