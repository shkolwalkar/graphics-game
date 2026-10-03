import java.awt.*;

public class Graphics {
   public static final double acceleration = -9.81; // for problem 4
   
   public static void main(String[] args) {
   
      /*
      DrawingPanel panel = new DrawingPanel(400, 400);
      panel.setBackground(Color.CYAN);
      
      Graphics2D g = panel.getGraphics();
      problemOne(g,0,0,100,1,5);
      problemOne(g,150,20,240,6,5);
      problemOne(g,10,120,120,5,4);
      problemOne(g,130,275,108,3,3);
      
      DrawingPanel panel2 = new DrawingPanel(420,300);
      panel2.setBackground(Color.DARK_GRAY);
      Graphics2D h = panel2.getGraphics();
      
      // init x = 0, init y = 0; size = 100, numElems = 1
      problemThree(h,0,0,100,5);
      problemThreeHelper(h,20,120,135,9,3);
      problemThreeHelper(h,180,30,200,5,4);
      */
      
      DrawingPanel panel3 = new DrawingPanel(420,220);
      panel3.setBackground(Color.WHITE);
      Graphics2D j = panel3.getGraphics();
      otherProblemFour(j,30,50,10);
      
   }
   
   public static void problemOne(Graphics2D g, int x, int y, int size, int numElems, int circles) {
      int elemLen = size / numElems;
      int smallest = elemLen / circles;
      g.setColor(Color.GREEN);
      g.fillRect(x,y,size,size);
      for (int j = 0; j < numElems; j++) {
         for (int k = 0; k < numElems; k++) {
            g.setColor(Color.YELLOW);
            g.fillOval(x + elemLen * j, y + elemLen * k, elemLen, elemLen);
            g.setColor(Color.BLACK);
            g.drawLine(x + elemLen * j, y + elemLen * k + (elemLen / 2), x + elemLen * (j + 1), y + elemLen * k + (elemLen / 2));
            g.drawLine(x + elemLen * j + (elemLen / 2), y + elemLen * k, x + elemLen * j + (elemLen / 2), y + elemLen * (k + 1));
            for (int l = 0; l <= circles; l++) {
               g.drawOval((x + (elemLen * j) + (elemLen / 2)) - (smallest / 2) * l,(y + (elemLen * k) + (elemLen / 2)) - (smallest / 2) * l, smallest * l, smallest * l);
            }
         }
      }
      g.drawRect(x,y,size,size);
   }
   
   public static void problemThree(Graphics2D g, int x, int y, int size, int numElems) {
      g.setColor(Color.BLACK);
      int elemLen = size / numElems;
      for (int i = 0; i < numElems; i++) {
         for (int j = 0; j < numElems; j++) {
            g.setColor(Color.BLACK);
            g.drawRect(x + (i * elemLen), y + (j * elemLen), elemLen, elemLen);
            int sum = i + j;
            if (sum % 2 == 0) {
               g.setColor(Color.LIGHT_GRAY);
            }
            else g.setColor(Color.WHITE);
            g.fillRect(x + (i * elemLen), y + (j * elemLen), elemLen, elemLen);
            
            g.setColor(Color.BLACK);
            g.drawLine(x + (i * elemLen), y + ((j + 1) * elemLen), x + ((i + 1) * elemLen), y + ((j + 1) * elemLen));
            g.drawLine(x + ((i + 1) * elemLen), y + (j * elemLen), x + ((i + 1) * elemLen), y + ((j + 1) * elemLen));
         }
      }   
   } 
   
   public static void problemThreeHelper(Graphics2D h, int x, int y, int size, int numElems, int elemsPerElem) {
      int elemLen = size / numElems;
      for (int i = 0; i < size; i += elemLen) {
         for (int j = 0; j < size; j += elemLen) {
            problemThree(h, i + x, j + y, elemLen, elemsPerElem);
         }
      }
      for (int i = 0; i < size; i += elemLen) {
         for (int j = 0; j < size; j += elemLen) {
            h.drawLine((i + x) + elemLen, j + y, (i + x) + elemLen, (j + y) + elemLen);
            h.drawLine(i + x, (j + y) + elemLen, (i + x) + elemLen, (j + y) + elemLen);
         }
      }
   }
   
   /*
   public static void problemFour(Graphics2D g, double velocity, double angle, int steps) {
      final double xVelocity = velocity * Math.cos(Math.toRadians(angle));
      double initYVelo = velocity * Math.sin(Math.toRadians(angle));
      double yVelocity = initYVelo;
      
      double timeToLand = (yVelocity * -1) * 2 / acceleration;
      double xAtLand = xVelocity * timeToLand;
      System.out.println(timeToLand + " " + xAtLand);
      double scale = 400 / xAtLand;
      System.out.println(scale);
      
      int pixelsPerStep = (int)xAtLand / steps;
      double timePerStep = pixelsPerStep / xVelocity;
      // System.out.println(timePerStep);
      double totalTime = 0;
      int lastX = 0;
      int lastY = 0;
      g.setColor(Color.BLACK);
      
      for (int currentPosition = 0; currentPosition <= xAtLand; currentPosition += pixelsPerStep) {
         totalTime += timePerStep;
         lastX = (int)(currentPosition);
         yVelocity = initYVelo + (acceleration * totalTime);
         lastY = (int)((lastY + (int)(yVelocity * timePerStep)) * scale);
         System.out.println("(" + lastX + "," + lastY + ")");
         g.drawOval(((int)(lastX * scale) - 2) + 20, 200 - ((int)(lastY * scale) - 2), 4, 4);
         g.fillOval(((int)(lastX * scale) - 2) + 20, 200 - ((int)(lastY * scale) - 2), 4, 4);
      }
   }
   */
   
   public static void otherProblemFour(Graphics2D g, double velocity, double angle, int steps) {
      double xVelocity = velocity * Math.cos(angle);
      double yVelocity = velocity * Math.sin(angle);
      double totalTime = -2.0 * yVelocity / acceleration;
      double timeIncrement = totalTime / steps;
      double xIncrement = xVelocity * timeIncrement;
      double scaleX = 400 / (-1 * xVelocity * totalTime);
      double maxY = (-1 * Math.pow(yVelocity,2)) / (2 * acceleration);
      double scaleY = 200 / maxY;
      // System.out.println(xIncrement + " " + scale);
      double x = 0.0;
      double y = 0.0;
      double t = 0.0;

      for (int i = 0; i <= steps; i++) {
         if (i > 0) {
            t += timeIncrement;
            x += (-1 * xIncrement);
            y = yVelocity * t + 0.5 * acceleration * t * t;
         }
         g.fillOval((int)(x * scaleX), (int)(200 - (y * scaleY)), 4, 4);
      }
   }
   
   
   
}
