/*
PokemonArena.java
Sat Arora
Pokemon Project
This is a project used to mimic a pokemon battle. The user has 4 pokemon to start, while the enemy has all the others.
Pokemons have certain attributes that can help/harm them in battle, such as their type, resistance, weakness, and specialties in attacks.
This program is used by playing with a console. Can you be crowned the Ultimate Trainer?
*/

//importing necessary packages
import java.io.*;
import java.util.*;
//PokemonArena class holds the main details, and is the control center for the entire battle
public class PokemonArena {
    static ArrayList<Pokemon> userPoks = new ArrayList<Pokemon>(); //userPoks is the arrayList of pokemon that the user possesses
    static ArrayList<Pokemon> enemyPoks = new ArrayList<Pokemon>(); //arrayList of pokemon that the computer possesses
    //random function to return a value in the range [low,high]
    public static int randint(int low, int high) {
        return (int)(Math.random()*(high-low+1)+low);
    }
    //user choosing who goes into battle method
    public static Pokemon userChoose() {
        Scanner kb = new Scanner(System.in);
        Pokemon pok;
        System.out.println("Choose the pokemon to go into the battle!");
        for (int i = 0; i < userPoks.size(); i++) {
            System.out.printf("%d -> %s\n",i,userPoks.get(i).getName()); //displaying nicely
        }
        int index = -1;
        while (true) {
            try {
                index = Integer.parseInt(kb.next()); //will go to the except if kb.next() can't be written as an integer
                if (index >= 0 && index < userPoks.size()) { //checking if index is a valid index in userPoks
                    pok = userPoks.get(index); //setting the user pokemon to the value at the index given in userPoks
                    System.out.printf("%s, I choose you!\n", pok.getName());
                    break; //valid to leave, as the pokemon for the user is now determined
                }
                System.out.println("Try again, bad input.");
            } catch (NumberFormatException ex) { //catches this if the input was another type besides int
                System.out.println("BAD INPUT");
            }
        }
        return pok; //returning the chosen one
    }
    //main method that controls the running of the battles, and also the determining of a winner
    public static void main(String[] args) throws IOException {
        Scanner kb = new Scanner(System.in);
        load(); //loading from pokemon.txt
        choose(); //choosing the 4 pokemon that will go into battle
        while (true) { //running as long as the enemy and user have at least 1 pokemon left to use (THIS IS THE SERIES OF BATTLES)
            if (enemyPoks.size() == 0) { //enemyPoks having nothing in it means everything was eliminated, so the user wins
                System.out.println("You win! You have achieved the title of trainer supreme!");
                break;
            }
            else if (userPoks.size() == 0) { //userPoks having nothing in it means everything was eliminated, so the enemy wins
                System.out.println("The computer wins! Bye bye now.");
                break;
            }
            int userGoesFirst = randint(0, 1); //random choice determining who goes first (1 --> user, 0 --> enemy)
            int index = -1,comIndex = randint(0,enemyPoks.size()-1); //index is temporarily declared to -1, but used for user choice for pokemon to go into battle. comIndex is the random choice which to go into battle.
            Pokemon user, com = enemyPoks.get(comIndex),dead; //user is the pokemon at the index, and com is the pokemon at the random index generated

            System.out.println("Energy Stats!");
            for (int i = 0; i < userPoks.size(); i++) {
                Pokemon pok = userPoks.get(i); //temporary Pokemon object used at the index in userPoks
                System.out.printf("%d -> %s\n",i,pok.getName()); //printing the series of values that will give respect to what pokemon they want
                pok.setEnergy(50); //setting energy to 50, as all battles need all pokemon to start off with 50 energy
                pok.unDisable(); //undisabling the pokemon
                userPoks.set(i,pok); //setting the object indexed at i in userPoks to the updated version, with the max energy
            }
            System.out.printf("Enemy chooses %s!\n", com.getName());
            System.out.print((userGoesFirst == 1) ? "User" : "Com"); //prints who goes first
            System.out.println(" goes first!");
            boolean battleEnd = false; //boolean detecting when the battle is over
            while (!battleEnd) { //runs until valid input is given
                user = userChoose(); //choosing user pokemon
                //dead is the pokemon that has died in the clash, whether it was from the user or com original pokemon, or another that the user got from their deck
                if (userGoesFirst == 1)
                    dead = turn(user, com, userGoesFirst, false); //passing thru parameters of attacker (user,as they go first),defense(com, as they don't),integer holding value that they go first, and a false value for ranTwice (checking if there were an even number of hits for a round)
                else
                    dead = turn(com, user, userGoesFirst, false); //passing thru parameters of attacker (com,as they go first),defense(user, as they don't),integer holding value that they dont go first, and a false value for ranTwice (checking if there were an even number of hits for a round)
                for (int i = 0; i < userPoks.size(); i++) { //loop thru each pokemon in the user collection
                    if (userPoks.get(i).getName().equals(dead.getName())) { //checking if the pokemon in the collection has the same name as the pokemon that died
                        System.out.printf("Enemy:%s has died!\n", dead.getName());
                        userPoks.remove(i); //taking it out from the collection
                        break; //leaving loop as it cannot be found more than once
                    }
                }
                for (int i = 0; i < enemyPoks.size(); i++) { //loop thru each pokemon in the enemy collection
                    if (enemyPoks.get(i).getName().equals(dead.getName())) { //checking if the pokemon in the colleciton has the same name as the pokemon that died
                        enemyPoks.remove(i); //removing from collection
                        battleEnd = true; //the enemy died, battle is over
                        break; //leaving loop as it cannot be found more than once
                    }
                }
                for (int i = 0; i < userPoks.size(); i++) {
                    Pokemon temp = userPoks.get(i); //pokemon at index i in userPoks, used to increase health and then set the arrayList at that index to that object
                    temp.setHP(Math.min(temp.getHP() + 20, temp.getHpMax())); //add 20 to hp, up to a max of the hp max of the pokemon
                    userPoks.set(i, temp);
                }
                if (userPoks.size() == 0) battleEnd = true; //if user has no pokemon left the battle is over
            }
        }
        System.exit(0); //terminate program
    }
    //method that loads all pokemon from pokemon.txt
    public static void load() throws IOException{
        Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt"))); //new scanner
        int num_pokemon = inFile.nextInt(); //reading the number of pokemon that will be found
        inFile.nextLine(); //dummy input
        for (int i = 0; i < num_pokemon; i++) {
            String[] details = inFile.nextLine().split(","); //getting the details of the file's line corresponding to that pokemon
            //all values that needed to be converted are customized here in temporary variables, otherwise their values at those indices in the details were called feeded into the Pokemon contructor
            double hp = Integer.parseInt(details[1]); //hp corresponding to 2nd value, but in double because damage can be in decimal form
            int numAttacks = Integer.parseInt(details[5]); //integer holding the number of attacks
            ArrayList<String> attackName = new ArrayList<String>(), special = new ArrayList<String>(); //arrayLists that holds all attackNames of the attacks, followed by the names of the specials
            ArrayList<Integer> energyCost = new ArrayList<Integer>(), damage = new ArrayList<Integer>(); //arrayLists that hold all energy costs of the attacks, followed by how much damage each attack does to the defender
            for (int j = 0; j < numAttacks; j++) { //looping thru the and adding each element in order of appearance in the details
                attackName.add(details[6+j*4]); //adding the attack's attack name
                energyCost.add(new Integer(Integer.parseInt(details[7+j*4]))); //adding the attack's energyCost
                damage.add(new Integer(Integer.parseInt(details[8+j*4]))); //adding the attack's damage
                special.add(details[9+j*4]); //adding the attack's special
            }
            enemyPoks.add(new Pokemon(details[0], hp, details[2], details[3], details[4], numAttacks, attackName, energyCost, damage, special)); //adding all pokemon to enemy collection, as the user will remove 4 from their collection when choosing
        }
    }
    //Method for player to choose their 4 pokemon to go into battle
    public static void choose() {
        Scanner kb = new Scanner(System.in);
        System.out.println("Type in 4 numbers for your pokemon!");
        int inputCount = 0; //counting how many values are inputs valid for the user
        //cool printing
        for (int i = 0; i < enemyPoks.size(); i++) { //for loop that will print nicely
            try {
                System.out.printf("%-2d -> %-15s\t", i * 4, enemyPoks.get(i * 4).getName());
                System.out.printf("%-2d -> %-15s\t", i * 4 + 1, enemyPoks.get(i * 4 + 1).getName());
                System.out.printf("%-2d -> %-15s\t", i * 4 + 2, enemyPoks.get(i * 4 + 2).getName());
                System.out.printf("%-2d -> %-15s\t", i * 4 + 3, enemyPoks.get(i * 4 + 3).getName());
            } catch (IndexOutOfBoundsException ex) {
                continue;
            } //catches it if i*4 + 3, +2. +1, or +0 reach a value greater than the value of the index size
            System.out.println();
        }
        System.out.println();

        while (inputCount != 4) { //keeps running until 4 pokemon are taken
            try {
                int index = Integer.parseInt(kb.next()); //fails if the value isn't integer, which it will just ask again for input
                if (index >= 0 && index < enemyPoks.size() && !userPoks.contains(enemyPoks.get(index))) { //checking if the index is valid (not already chosen, valid index in bound)
                    System.out.printf("You have chosen %s\n",enemyPoks.get(index).getName());
                    inputCount++; //incrementing because a valid choice has been made
                    userPoks.add(enemyPoks.get(index)); //adding the choice to the user collection
                }
                else System.out.println("Bad input");
            }
            catch (NumberFormatException ex) {System.out.println("BAD INPUT");} //runs if the input is not integer type
        }
        for (Pokemon pok : userPoks) {
            enemyPoks.remove(enemyPoks.indexOf(pok)); //removing all pokemon that are in user hand from the enemy hand
        }
    }
    //receives the attacking and defending pokemon, integer detecting if user is currently attacking, and ran twice boolean held to determine whether will be completed after this turn is made
    public static Pokemon turn(Pokemon attacker, Pokemon defender, int userTurn,boolean ranTwice) {
        boolean killed = false; //boolean used to check if the one on the defense is dead yet
        Scanner kb = new Scanner(System.in);
        boolean canAttack = false, canRetreat = false; //canAttack is true if the attacker has enough energy to perform at least one of their attacks, canRetreat is true if the attacker has a teammate (at least one)
        if (attacker.validAttacks().size() > 0) canAttack = true; //if the number of possible attacks to use is greater than 0 (none), then attacker can attack
        if (userTurn == 1 && userPoks.size() > 1) canRetreat = true; //if the attacker's team has someone that can step in, they can retreat
        if (userTurn == 0 && !defender.getStunned()) { //if enemy is attacking, and they were not stunned
            while (true) { //loops until an action is actually made
                int randChoice = randint(1, 2);
                if (randChoice == 2 && canAttack) { //if the determined choice was attacking, and they can do so
                    System.out.println("The com will attack!");
                    killed = attacker.chooseAttack(attacker, defender, userTurn); //performing the attack
                    break; //breaking because the action was made
                }
                if (randChoice == 1) { //passing turn
                    System.out.println("The com will pass!");
                    break; //breaking because the action was made
                }
            }
        }
        else if (userTurn == 0) { //if it is enemy's turn, but they are stunned
            System.out.println("Enemy can't attack until next turn!");
            attacker.setStunned(false); //now that they are not stunned, they can continue and make a move on their next turn, if it comes
        }
        if (userTurn == 1 && !attacker.getStunned()) { //if the user has chosen to attack attacking and not stunned
            if (canRetreat) System.out.println("Enter R to retreat!");
            if (canAttack) System.out.println("Enter A to attack!");
            System.out.println("Enter P to pass!");
            while (true) {
                String choice = kb.next();
                if (choice.toUpperCase().equals("R") && canRetreat) { //if they enter the corresponding input, and it was determined they could retreat
                    System.out.println("You shall retreat!");
                    attacker = retreat(attacker); //calling retreat function
                    break; //action was made, so we can break
                }
                else if (choice.toUpperCase().equals("A") && canAttack) { //if they enter the corresponding input, and it was determined they could attack
                    System.out.println("You shall attack!");
                    killed = attacker.chooseAttack(attacker, defender, userTurn); //performing attack
                    break; //action was made, so we can break
                }
                else if (choice.toUpperCase().equals("P")) { //if they enter the corresponding input
                    System.out.println("You are going to pass? Better luck next turn.");
                    break; //action was made, so we can break
                }
                else System.out.println("Invalid input."); //reaches here if input was not valid
            }
        }
        else if (userTurn == 1) { //if user should attack, but they are stunned
            System.out.println("User can't attack until next turn!");
            attacker.setStunned(false); //their move is passed
        }
        if (killed) return defender; //returning the defender as the pokemon that will be removed from the hand it comes from because they were killed in a clash
        if (ranTwice) { //if a round has completed
            for (int i = 0; i < userPoks.size(); i++) { //going thru each index in userPoks
                Pokemon temp = userPoks.get(i); //temporary Pokemon object that will have a modification on the energy, and then put back at the index
                temp.setEnergy(Math.min(temp.getEnergy() + 10, 50)); //adding 10 to the energy after each round, max of 50
                userPoks.set(i,temp);
                System.out.printf("%s Energy Level -> %d\n",userPoks.get(i).getName(),userPoks.get(i).getEnergy());
            }
        }
        return turn(defender,attacker,1-userTurn,!ranTwice); //running the turn again, except with the attacker and defender switching roles, and userTurn and ranTwice would also be the opposite because you alternate on yes and no for both
    }
    //method that takes a pokemon and returns a new pokemon that will take its place in the fight. this action can only be done by the user, so the code simplifies a bit
    public static Pokemon retreat(Pokemon curPok) { //curPok is the current pokemon in the battle, wishing to leave
        int badInput = -1, input; //badInput is the index of the curPok (one that can't be chosen), while input will hold the index of the integer chosen
        Pokemon newPok = curPok; //newPok pokemon is the pokemon object that will be used, temporarily set to the current pokemon in battle
        Scanner kb = new Scanner(System.in);
        for (int i = 0; i < userPoks.size(); i++) {
            if (!userPoks.get(i).getName().equals(curPok.getName())) { //checking if the pokemon in the pokemon ArrayList does not have the same name as the pokemon that wishes to leave, as that is not allowed to be chosen
                System.out.printf("%d -> %s\n",i,userPoks.get(i).getName());
            }
            else badInput = i; //otherwise, the input in range that is not allowed is that index
        }
        while (true) { //runs until valid input is received
            try {
                input = Integer.parseInt(kb.next()); //getting input, storing the integer of it to input (goes to catch in kb.next() is not of integer type)
                if (input >= 0 && input < userPoks.size() && input != badInput) { //check if the input is in the size of userPoks, and if it's not the index of the pokemon already in battle
                    newPok = userPoks.get(input); //setting the new one in battle to be the index at the old one
                    break;
                }
                System.out.println("Bad input!");
            }
            catch (NumberFormatException ex) {System.out.println("BAD INPUT");}; //for the cases where the input was not an integer
        }
        System.out.printf("%s will now be replaced by %s!\n",curPok.getName(),newPok.getName());
        System.out.println("User:");
        newPok.stats(); //printing stats just so the user is clued in on what they are doing
        return newPok; //returning the new pokemon
    }
}
//Pokemon class holds the properties of each pokemon object. Methods for getting valid attacks, disabling pokemon strategically, performing the attack (no multiplier here), and getting stats of the pokemon are methods used to simplify other classes/methods.
class Pokemon {
    //FIELDS ARE ALL PROPERTIES IN THE FILE, except those that weren't are explained
    private String name, type, resistance, weakness;
    private double hp, hpMax; //hpMax set to the current hp, as it cant exceed it
    private int energy, numAttacks;
    private ArrayList<String> attackName, special;
    private ArrayList<Integer> energyCost, damage;
    private boolean stunned, disabled; //booleans if the pokemon is currently stunned or disabled
    //constructor
    public Pokemon(String name, double hp, String type, String resistance, String weakness,
                  int numAttacks, ArrayList<String> attackName, ArrayList<Integer> energyCost, ArrayList<Integer> damage, ArrayList<String> special) {
        this.name = name;
        this.hp = hp;
        this.hpMax = hp;
        this.type = type;
        this.resistance = resistance;
        this.weakness = weakness;
        this.numAttacks = numAttacks;
        this.attackName = attackName;
        this.energyCost = energyCost;
        this.damage = damage;
        this.special = special;
        this.energy = 50;
        this.stunned = false;
        this.disabled = false;
    }
    //getter and setter methods to allow for all fields to be private to prevent outside users who dont know what they are doing to have full access to the variables
    public String getName() {return this.name;}
    public void setStunned(boolean value) {this.stunned = value;}
    public boolean getDisabled() {return this.disabled;}
    public void setDisabled() {this.disabled = true;}
    public void unDisable() {this.disabled = false;}
    public boolean getStunned() {return this.stunned;}
    public int getEnergy() {return this.energy;}
    public void setEnergy(int value) {this.energy = value;}
    public String getResistance() {return this.resistance;}
    public String getWeakness() {return this.weakness;}
    public String getType() {return this.type;}
    public double getHP() {return this.hp;}
    public void setHP(double value) {this.hp = value;}
    public double getHpMax() {return this.hpMax;}
    //valid attacks returns an arrayList with the indices in each of the 4 arrayLists giving the details for the attack, if they can use that attack or not
    public ArrayList<Integer> validAttacks() {
        ArrayList<Integer> attacks = new ArrayList<Integer>(); //arrayList holding all indices of attacks
        for (int i = 0; i < energyCost.size(); i++) { //looping thru the energyCost values
            if (this.energy >= energyCost.get(i)) attacks.add(new Integer(i)); //the only condition is if the attacker has enough energy, which it just needs at minimum the energy cost of the attack to perform the attack
        }
        return attacks;
    }
    //disablePokemon is a method that checks for disability, and if it is not then we decrease the power of each of the attacks by 10
    public void disablePokemon() {
        if (!this.disabled) { //checks if the pokemon was previously disabled
            for (int i = 0; i < damage.size(); i++) { //looping thru the damage of the attacks
                this.damage.set(i, new Integer(Math.max(this.damage.get(i) - 10,0))); //setting each one to be the maximum of 0 damage, or the current one minus 10
            }
        }
        this.disabled = true;
    }
    public void enablePokemon() {
        if (this.disabled) {
            for (int i = 0; i < damage.size(); i++) { //looping thru the damage of the attacks
                this.damage.set(i, new Integer(this.damage.get(i) + 10)); //setting each one to be the maximum of 0 damage, or the current one minus 10
            }
        }
        this.disabled = false;
    }
    //chooseAttack takes in the attacker and defender pokemon currently involved, as well as if the user is the one attacking.
    //allows player or computer to choose the attack, and gets data about how the attack went, and modifies the pokemon accordingly.
    //returns true if the defender takes a hit that kills it, and returns false if the battle goes on.
    public boolean chooseAttack(Pokemon attacker, Pokemon defender, int userTurn) {
        Scanner kb = new Scanner(System.in);
        ArrayList<Integer> attacks = attacker.validAttacks(); //getting the arrayList of valid attacks (by getting to this function, we already determined that at least one attack can be used_
        int option = -1; //option variable that will be the index of the attack in the valid attacks, inputted by user or computer
        if (userTurn == 0) { //check if computer is attacking
            option = PokemonArena.randint(0,attacks.size()-1); //random selection to be able to index the attack
            System.out.printf("The com has decided to use the %s attack!\n",attacker.attackName.get(option));
        }
        else {
            //nice printing for the next 5 lines for all the attacks
            System.out.printf("%-5s\t%-15s\t%-12s\t%-10s\t%-15s\n", "Input", "Attack Name", "Energy Cost", "Damage", "Special");
            for (int i = 0; i < attacks.size(); i++) {
                for (int j = 0; j < 70; j++) System.out.print("=");
                System.out.printf("\n%-5s\t%-15s\t%-12d\t%-10d\t%-15s\n", i, attackName.get(i), energyCost.get(i), damage.get(i), special.get(i));
            }
            while (true) { //keeps going until a valid index is reached
                try {
                    option = Integer.parseInt(kb.next()); //attempts to convert the string of input into an integer
                    if (option >= 0 && option < attacks.size()) break; //checks if the index is actually valid, and will break out of the while loop as then there would be a valid attack to use
                    System.out.println("Invalid input."); //prints this if the input was integer, but not a valid one.
                } catch (NumberFormatException ex) {
                    System.out.println("Bad input"); //gets to this point if the input was not an integer
                }
            }
        }
        Attack attack = new Attack(attacker.attackName.get(option),attacker.energyCost.get(option),attacker.damage.get(option),attacker.special.get(option)); //new attack object that will handle the special multiplications
        ArrayList<Pokemon> results = attack.specialMult(attacker,defender); //calling the special mult, in which the damage has already been done to the pokemon
        attacker = results.get(0); //new attacker details is the first element
        defender = results.get(1); //new defender details is the second element
        System.out.println((userTurn == 1) ? "User:" : "Enemy:");
        attacker.stats(); //getting the new stats of the atatcker
        if (userTurn == 1) { //checking if the user was attacking
            for (int i = 0; i < PokemonArena.userPoks.size(); i++) { //looping thru each index in userPoks
                if (PokemonArena.userPoks.get(i).equals(attacker.getName())) { //checking if the name of the attacker chosen is the same as the name in the index of the userPoks
                    PokemonArena.userPoks.set(i,attacker); //setting the new details of the attacker as the element in the arrayList
                    break; //breaking because there would be no more occurences
                }
            }
        }
        else {
            for (int i = 0; i < PokemonArena.enemyPoks.size(); i++) { //looping thru each index in enemyPoks
                if (PokemonArena.enemyPoks.get(i).equals(attacker.getName())) { //checking if the name of the attacker chosen is the same as the name in the index of the enemyPoks
                    PokemonArena.enemyPoks.set(i,attacker); //setting the new details of the attacker as the element in the arrayList
                    break; //breaking because there would be no more occurences
                }
            }
        }
        System.out.println((userTurn == 0) ? "User:" : "Enemy:");
        if (defender.hp <= 0) {
            System.out.println("Lost a pokemon!");
            return true; //since someone died, we return true that the battle is now over
        }
        else defender.stats(); //printing the remaining hp and energy of the defender, as the battle is not over
        return false; //meaning that the battle is not over
    }
    //stats method prints the hp and energy of the pokemon in a good-looking manner
    public void stats() {
        System.out.printf("HP: %.1f\nEnergy: %d\n",this.hp,this.energy);
    }
}
//attack class that holds information about the attack as fields, and is in the sole charge of amplifying the attacks
class Attack {
    private String special, name; //fields for name of special, and name of attack
    private int energyCost; //field for the cost of the attack
    private double dmg; //field for the damage done, held as double to have values that dont conflict with a 7.5 (doesnt round)
    public Attack(String name, int energyCost, int dmg, String special) { //constructor
        this.name = name;
        this.special = special;
        this.dmg = (double)dmg;
        this.energyCost = energyCost;
    }
    //specialMult that takes in attacker and defender pokemon, then amplifies the attack damage in any way possible, and then damages the pokemon accordingly. it reutrns an arrayList in form of (attacker,defender)
    public ArrayList<Pokemon> specialMult(Pokemon attacker, Pokemon defender) {
        int choice = PokemonArena.randint(0,1); //random function that could be used in many of the specials
        double mult = 1; //multiplier variable, could end in .5, so a double is used
        attacker.setEnergy(Math.max(0,attacker.getEnergy() - this.energyCost));
        if (special.equals("stun")) {
            if (choice == 0) System.out.println("The stun didn't hit!"); //the 50% chance that the stun didnt hit
            else { //in the 50% chance that the stun landed
                defender.setStunned(true); //stunning the pokemon for a turn
                System.out.printf("%s is now stunned for a turn!\n",defender.getName());
            }
        }
        if (special.equals("wild card")) {
            if (choice == 0) { //the 50% chance the attack does nothing
                System.out.println("The attack did not make contact!");
                mult = 0; //any amount of damage * 0 would be 0, so this is a fast way to make the attack have no effect
            }
            else System.out.println("The attack made contact!"); //in the 50% chance that the attack made contact
        }
        if (special.equals("wild storm")) {
            mult = 0; //mult becomes the number of times that the wild storm is completed
            while (choice > 0) { //keeps going until the 50% chance the wild storm is used is hit
                mult++; //showing the attack hit one more
                choice = PokemonArena.randint(0,1); //re-evaluating our chances to see if another wild storm will hit
            }
            System.out.printf("The attack made contact %d times!\n",(int)mult);
        }
        if (special.equals("disable")) {
            if (!defender.getDisabled()) { //checks if the opposing pokemon was already disabled, and there is no point of going through with it if it already has
                defender.disablePokemon();
                System.out.printf("%s is now disabled! All attacks do 10 less damage!\n",defender.getName());
            }
        }
        if (special.equals("recharge")) {
            attacker.setEnergy(Math.min(50,attacker.getEnergy()+20)); //recharging energy to its current one + 20, to a max of 50
            System.out.printf("%s has recovered energy to %d!\n",attacker.getName(),attacker.getEnergy());
        }
        if (defender.getResistance().equals(attacker.getType())) {
            System.out.printf("The attack damage is halved because %s has a resistance that is the same type as %s\n",defender.getName(),attacker.getName());
            mult /= 2; //the resistance allows the attack to be halved in damage

        }
        if (defender.getWeakness().equals(attacker.getType())) {
            System.out.printf("The attack damage is doubled because %s has a weakness that is the same type as %s\n",defender.getName(),attacker.getName());
            mult *= 2; //the weakness allows the attack to double in damage
        }
        dmg *= mult; //the multiplier has been set, so it is added to the dmg
        defender.setHP(defender.getHP()-dmg); //setting the hp of the defender
        ArrayList<Pokemon> results = new ArrayList<Pokemon>(); //pokemon arrayList that will hold the updated statistics of the pokemon
        results.add(attacker); //adding the attacker as first element
        results.add(defender); //adding the defender as the second element
        return results; //returning the pokemon with new their new energy and hp levels
    }
}