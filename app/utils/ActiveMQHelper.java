package utils;

public class ActiveMQHelper {/*
	
    private static String brokerUrl;
    private ActiveMQConnection connection;
    private Session session;
    
	public boolean connect() {
		brokerUrl = Jws.configuration.getProperty("activemq.brokerURL");
		if(StringUtils.isEmpty(brokerUrl)) {
        	Logger.error("activemq.brokerURL为空");
        	return false;
        }
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, brokerUrl);
		try {
			connection = (ActiveMQConnection) factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			return true;
		} catch (Exception e) {
			 Logger.error("消息队列连接出现异常");
			 e.printStackTrace();
		}
		return false;
	}
	
    public Message getMessage(String queueName) throws JMSException {
		Message message = null;
		Destination dest = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(dest);
		message = consumer.receive(1000);
		consumer.close();

		return message;
	}
    
    public TextMessage getTextMessage(String queueName) throws JMSException {
		Message message = null;
		Destination dest = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(dest);
		message = consumer.receive(1000);
		consumer.close();

		return (TextMessage)message;
	}
    
    public void setTextMessage(String queueName, String message) throws JMSException {
		Destination dest = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		TextMessage tmsg = session.createTextMessage(message);
		producer.send(tmsg);
		producer.close();
	}
    
    public void setMessage(String queueName, Serializable message) throws JMSException {
		Destination dest = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		BytesMessage bmsg = session.createBytesMessage();
		bmsg.writeBytes(SerializerUtils.serialize(message));
		producer.send(bmsg);
		producer.close();
	}
    
    public boolean disconnect() {
		try {
			if (connection != null) {
				connection.close();
			}
			return true;
		} catch (JMSException e) {
			Logger.error("断开消息队列出现异常");
            e.printStackTrace();
		}
		return false;
	}

*/}
