package com.qhl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Main {
//多线程
    public static void main(String[] args) throws InterruptedException {
        long ans = 0;
        Scanner scanner = new Scanner(System.in);
        int x = scanner.nextInt();
        long n =  1000000000;
        int cpuCount = Runtime.getRuntime().availableProcessors();
        long step = n / cpuCount;
        long from = 1;
        long to;
        Vector<Long> Answers = new Vector<Long>();

        ArrayList<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < cpuCount; i++)
        {
            to = from+step;
            if (i == cpuCount - 1)
                to = n;
            System.out.println("Start calculating from: "+from +" to: " +to);
            Thread t = new Thread(new CalcThread(from, to, x, Answers));
            from += step;
            threadList.add(t);
            t.start();
        }
        for (int i = 0; i < threadList.size(); i++)
        {
            threadList.get(i).join();
        }
        for (int i = 0; i < Answers.size(); i++)
        {
            ans += Answers.get(i);
        }
        System.out.println(ans);
    }
}

class CalcThread implements Runnable {
    public long ans = 0;
    public long From, To;
    public int X;
    public Vector<Long> FinalAnswer;

    public void run()
    {
        for (long i = From; i < To; i++) {
            if (contain(i, X)) ans += i;
        }
        System.out.println(ans);
        FinalAnswer.add(ans);
    }

    public CalcThread(long _from, long _to, int _x, Vector<Long> _finalAnswer)
    {
        From = _from;
        To = _to;
        X = _x;
        FinalAnswer = _finalAnswer;
    }

    static boolean contain(long num, int x)
    {
        return String.valueOf(num).contains(String.valueOf(x));
    }
}
//炸鸡店


abstract class Drinks
{
    public String Name = "";
    public double Cost = 0;
    public LocalDate ProductionDate = LocalDate.now();
    public Integer LifeSpan = 0;

    public Drinks(String _name, double _cost, LocalDate _productionDate, Integer lifeSpan)
    {
        Name = _name;
        Cost = _cost;
        ProductionDate = _productionDate;
    }

    public String toString()
    {
        return "饮料: " + Name + "\n成本: " + Cost + "\n生产日期: " + ProductionDate.toString();
    }
}

class Beer extends Drinks {
    public double Degree;

    public Beer(double _cost, double _degree, LocalDate productionDate)
    {
        super("啤酒", _cost, productionDate, 30);
        Degree = _degree;
    }
}

class Juice extends Drinks {

    public Juice(double _cost, LocalDate productionDate)
    {
        super("果汁", _cost, productionDate, 2);
    }
}

class SetMeal
{
    public String Name = "";
    public double Price = 0;
    public String FriedChicken = "";
    Drinks Drink;
    public String toString() {
        return "套餐: " + Name + "\n价格: " + Price + "\r炸鸡: " + FriedChicken
                + "饮料: \n" + Drink.toString();
    }

     public SetMeal(String _name, double _price, String _friedChicken, Drinks _drink)
     {
         Name = _name;
         Price = _price;
         FriedChicken = _friedChicken;
         Drink = _drink;
     }
}

interface FriedChickenRestaurant
{
    public void SellSetMeal(String name);
    public void FillStock(ArrayList<Drinks> drinks);
}

class IngredientSortOutException extends RuntimeException
{
    public IngredientSortOutException(String ingredientName)
    {
        super(ingredientName + " 库存不足！");
    }
}

class OverdraftBalanceException extends RuntimeException
{
    public OverdraftBalanceException(String stuffName)
    {
        super("进货" + stuffName + "时库存不足！");
    }
}

class West2FriedChickenRestaurant implements FriedChickenRestaurant
{
    public double Balance = 0;
    ArrayList<Beer> BeerList;
    ArrayList<Juice> JuiceList;
    static ArrayList<SetMeal> SetMealList;
    static {
         SetMealList = new ArrayList<SetMeal>() {{
                 new SetMeal("经典套餐",10, "原味炸鸡",
                         new Beer(3,2.0, LocalDate.now()));
         }};
    }


    public void SellSetMeal(String name)
    {
        for (SetMeal sm : SetMealList)
        {
            boolean founded = false;
            if (sm.Name.equals(name))
            {
                if (sm.Drink instanceof Beer)
                {
                    for (Beer b : BeerList)
                    {
                        if (b.Name == sm.Drink.Name)
                        {
                            BeerList.remove(b);
                            founded = true;
                        }
                    }
                    if (!founded)
                    {
                        throw new IngredientSortOutException(sm.Drink.Name);
                    }
                }
            }
            else
            {
                for (Juice b : JuiceList)
                {
                    if (b.Name == sm.Drink.Name)
                    {
                        JuiceList.remove(b);
                        founded = true;
                    }
                }
                if (!founded)
                {
                    throw new IngredientSortOutException(sm.Drink.Name);
                }
            }
        }
    }


    public void FillStock(ArrayList<Drinks> drinks)
    {
        for (Drinks d : drinks)
        {
            if (d instanceof Beer)
            {
                BeerList.add((Beer)d);
            }
            else
            {
                JuiceList.add((Juice)d);
            }
        }
    }

    void use(Beer beer)
    {
        if (BeerList.size() > 0)
        {
            boolean founded = false;
            for (Beer b : BeerList) {
                if (b.Name.equals(beer.Name))
                {
                    BeerList.remove(b);
                    founded = true;
                    break;
                }
            }
            if (!founded)
                throw new IngredientSortOutException("啤酒");
        }
        else
        {
            throw new IngredientSortOutException("啤酒");
        }
    }

    void use(Juice juice)
    {
        if (JuiceList.size() > 0)
        {
            boolean founded = false;
            for (Juice b : JuiceList) {
                if (b.Name.equals(juice.Name))
                {
                    JuiceList.remove(b);
                    founded = true;
                    break;
                }
            }
            if (!founded)
                throw new IngredientSortOutException("果汁");
        }
        else
        {
            throw new IngredientSortOutException("果汁");
        }
    }
}
