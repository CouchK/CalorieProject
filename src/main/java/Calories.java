public class Calories
{
    double totalCals;

    public Calories()
    {
        totalCals = 0.0;
    }

    public void addCalories(double cal)
    {
        totalCals += cal;
    }

    public double getCalories()
    {
        return totalCals;
    }

    public void deleteCalories(double cal)
    {
        totalCals -= cal;
    }
}
