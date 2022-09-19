# Natural Selection Simulator

This repository is a backup of my 11th grade AP Computer Science A final project I managed to dig up.

## Description

I chose to make this project because I was inspired by a YouTube video I had recently watched, ["Simulating Natural Selection" by Primer](https://www.youtube.com/watch?v=0ZGbIKd0XrM). Some logic was taken directly from the video, like the energy expenditure model for the [creatures](Natural%20Selection%20Simulator/src/Creature.java):

```java
// Helper method for expendEnergy(); also returns value without changing energy
public int calculateCost(int stepsTaken) {
    int expense = stepsTaken*(speed*speed + size*size*size);
    return expense;
}
```

However, since mine was a high school project done in, quite literally, my first year of programming ever, I made many simplifications. For example, the [habitat](Natural%20Selection%20Simulator/src/Habitat.java) was naively implemented with a small 2D array, and I imagine the small size of the grid definitely hindered the results. I also did not implement the "Sense" attribute of the creatures. I also use a very simple GUI built with the built-in [java.awt package](https://docs.oracle.com/javase/7/docs/api/java/awt/package-summary.html) instead of any fancy graphics or data visualization.

Unfortunately, I don't think this program yields any useful inferences or conclusions about natural selection. I recall from the experience that the program seemed to output results that were more random than deterministic. For example, varying the speed of the creatures was intended to demonstrate effect on energy expenditure and how it may be more or less efficient in denser/sparser communities, but conducting multiple runs seemed to yield contradictory results.

Nevertheless, I'm quite proud with the outcome as it was my first ever "real" programming project.

## Development

I developed this program with the Eclipse IDE, so the [Natural Selection Simulator](Natural%20Selection%20Simulator) directory, with the exception of the ignored bytecode bin/ files, is copy-pasted directly from my eclipse-workspaces directory. Supposedly placing it in another workspaces directory could let you access it like any other project, but I have not tested this.
