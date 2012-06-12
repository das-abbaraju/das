package com.picsauditing.messaging;

import com.picsauditing.jpa.entities.Message;

public interface Publisher {
	void publish(Message message);
}
