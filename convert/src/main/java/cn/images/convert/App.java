package cn.images.convert;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import com.github.gotson.nightmonkeys.heif.imageio.plugins.HeifImageReaderSpi;


/**
 * Hello world!
 *
 */
public class App {

//	public static void main1(String[] args) {
//		try {
//			byte[] heicData = Files.readAllBytes(Paths.get("d:/1.heic"));
//			HeifDirectory directory = new HeifReader().extract(heicData);
//			
//		} catch(Exception x) {
//			x.printStackTrace();
//		}
//	}
	public static void main(String[] args) {
		// 注册 HEICImageReaderSpi
		IIORegistry registry = IIORegistry.getDefaultInstance();
		registry.registerServiceProvider(new HeifImageReaderSpi());
		try {
			// 读取 heic 图片
			File inputFile = new File("d:/1.heic");
			System.out.println(inputFile.exists());
			//BufferedImage image = ImageIO.read(inputFile);
			BufferedImage image = ImageIO.read(new File("d:/1.heic"));
			System.out.println(image.getHeight());
			// 创建 jpg 格式的 BufferedImage
			BufferedImage jpgImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_3BYTE_BGR);
			jpgImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

			// 注册 HEICImageWriterSpi
			registry.registerServiceProvider(new HeifImageReaderSpi());

			// 创建输出文件
			File outputFile = new File("d:/output.jpg");

			// 将图片保存为 jpg 格式
			ImageIO.write(jpgImage, "jpg", outputFile);

			System.out.println("转换完成！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
