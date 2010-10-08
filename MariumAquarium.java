import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MariumAquarium extends JComponent implements Runnable
{
	private static final long serialVersionUID = 410095910584114653L;
	private static final String BACKGROUND_IMAGE = "images/background.gif";
	private static final String FISH_IMAGES_FOLDER = "images/fish";
	private static final String WINDOW_TITLE = "Marium Aquarium";

	private static final Dimension AQUARIUM_SIZE = new Dimension(800, 600);
	private static final int DEFAULT_TOTAL_FISH = 25;
	private static final int SLEEP_TIME = 100;
	private static final int INSET = 20;

	private Image backgroundImage;
	private Image[] fishImages;
	private List<Fish> fish;
	private Random random;

	public MariumAquarium(Image backgroundImage, int numberFish)
	{
		this.backgroundImage = backgroundImage;
		this.random = new Random(System.nanoTime());
		this.setSize(AQUARIUM_SIZE);
		this.fishImages = loadFishImages();
		this.fish = createFish(numberFish);

		startAnimation();
	}

	private List<Fish> createFish(int totalFish)
	{
		final Rectangle boundaries = calculateBoundaries();
		List<Fish> creatures = new ArrayList<Fish>();
		
		for(int i = 0; i < totalFish; i++)
		{
			Image fishImage = getRandomFishImage();
			creatures.add(new Fish(fishImage, boundaries));
		}
		
		return creatures;
	}
	
	private Image[] loadFishImages()
	{
		File folder = new File(FISH_IMAGES_FOLDER);
		File[] imageFiles = folder.listFiles();
		Image[] images = new Image[imageFiles.length];
		
		for(int i = 0; i < imageFiles.length; i++)
		{
			images[i] = loadImage(imageFiles[i].getAbsolutePath());
		}
		
		return images;
	}

	private Image getRandomFishImage()
	{		
		int index = random.nextInt(fishImages.length);
		return fishImages[index];
	}
	
	private Rectangle calculateBoundaries()
	{
		Insets inset = getInsets();
		Rectangle boundaries = new Rectangle(inset.left, inset.top, 
						     getSize().width - (inset.left + inset.right), 
						     getSize().height - (inset.top + inset.bottom));
		return boundaries;
	}
	
	private void startAnimation()
	{
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	public void run()
	{
		while(Thread.currentThread().isAlive())
		{
			moveFish();
			repaint();
			sleep();
		}
	}
	
	private void moveFish()
	{
		for(Fish f : fish)
		{
			f.swim();
		}
	}
	
	private void sleep()
	{
		try
		{
			Thread.sleep(SLEEP_TIME);
		}
		catch(Exception e){}
	}

	public void paint(Graphics canvas) 
	{
		canvas.drawImage(backgroundImage, 0, 0, this);

		for(Fish f : fish)
		{
			Image fishImage = f.getImage();
			Point pos = f.getPosition();
			Direction direction = f.getDirection();
			
			if(direction.equals(Direction.WEST))
			{
				canvas.drawImage(fishImage, pos.x, pos.y, this);
			}
			else
			{
				int height = fishImage.getHeight(null);
				int width = fishImage.getWidth(null);

				//Draw Image flipped horizontally
				canvas.drawImage(fishImage, pos.x, pos.y, 
								 pos.x + width, pos.y + height, 
								 width, 0, 0, height, this);
			}
		}
	}
	
	private static Image loadImage(String filename)
	{
		return Toolkit.getDefaultToolkit().getImage(filename);
	}
	
	public static void main(String[] args)
	{
		int totalFish = (args.length > 0) ? Integer.parseInt(args[0]) : 
											DEFAULT_TOTAL_FISH;
		Image aquariumBackground = loadImage(BACKGROUND_IMAGE);
		MariumAquarium aquarium = new MariumAquarium(aquariumBackground, totalFish);
		
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setContentPane(aquarium);
		frame.setSize(aquarium.getWidth(), 
					  aquarium.getHeight() + INSET);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
