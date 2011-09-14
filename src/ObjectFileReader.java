/* SWEN20003 Object Oriented Software Development
 * ObjectFileInput Class
 * Author: Aram Kocharyan <aramk>
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Responsible for reading in input from a text file containing object
 * information and creating the player, objects, items and checkPoints in the
 * world parameter given. Allows given numeric values to be either Integer or
 * Double. Parses them as Double and sends as arguments. THIS IS A UTILITY
 * CLASS, SO IT CONTAINS ONLY ONE STATIC METHOD. IT CANNOT BE INSTANTIATED.
 */
public class ObjectFileReader {
	public static void readInput(World world, String filename) {
		try {
			// open the file
			FileInputStream inputFile = new FileInputStream(filename);
			DataInputStream inputData = new DataInputStream(inputFile);
			BufferedReader inputReader = new BufferedReader(
					new InputStreamReader(inputData));
			// strings used to parse
			String stringLine;
			StringTokenizer stringToken;
			String[] words;
			// read each line until EOF or blank line
			while ((stringLine = inputReader.readLine()) != null) {
				stringToken = new StringTokenizer(stringLine, " ,");
				words = new String[3];
				// read the first 3 words on each line
				for (int word = 0; stringToken.hasMoreTokens() && word < 3; word++) {
					words[word] = stringToken.nextToken();
				}
				// if encountering a comment, skip line
				if (words[0].substring(0, 1).equals("#")) {
					continue;
				}
				// parse input
				switch (Game.inputType.valueOf(words[0].toUpperCase())) {
				// create objects
				case PLAYER:
					// only allow one player
					if (world.getPlayer() != null) {
						System.err.println("Error: More than one player specified");
						return;
					} else {
						world.setPlayer(new Player(
								Double.parseDouble(words[1]),
								Double.parseDouble(words[2]), world));
					}
					break;
				case FIGHTER:
					world.addObject(new Fighter(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				case DRONE:
					world.addObject(new Drone(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				case ASTEROID:
					world.addObject(new Asteroid(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				case BOSS:
					if (world.getBoss() != null) {
						System.err .println("Error: More than one boss specified");
						return;
					} else {
						world.setBoss(new Boss(Double.parseDouble(words[1]),
								Double.parseDouble(words[2]), world));
					}
					break;
				case TURRET:
					world.addObject(new Turret(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				// create items
				case REPAIR:
					world.addObject(new Repair(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				case SHIELD:
					world.addObject(new Shield(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				case FIREPOWER:
					world.addObject(new FirePower(Double.parseDouble(words[1]),
							Double.parseDouble(words[2]), world));
					break;
				// create checkpoints
				case CHECKPOINT:
					world.addCheckPoint(Double.parseDouble(words[1]));
					break;
				}
			}
			// close input stream
			inputData.close();
		} catch (Exception e) {
			// if we encounter an error, print it
			System.err.println("Error: " + e.getMessage());
		}
	}

}
