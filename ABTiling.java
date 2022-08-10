
/*
 * Increase the number in line 60 to generate more tiles. In line 217 change the number to change the size ofhe tiles.
 *          */
package test1;

import java.awt.*;
import java.util.List;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import static java.lang.Math.*;
import static java.util.stream.Collectors.toList;
 
public class KiteDart extends JPanel {
    // ignores missing hash code
    class Tile {
        double x, y, angle, size;
        Type type;
 
        Tile(Type t, double x, double y, double a, double s) {
            type = t;
            this.x = x;
            this.y = y;
            angle = a;
            size = s;
        }
 
        @Override
        public boolean equals(Object o) {
            if (o instanceof Tile) {
                Tile t = (Tile) o;
                return type == t.type && x == t.x && y == t.y && angle == t.angle;
            }
            return false;
        }
    }
 
    enum Type {
        Fat, Thin
    }
 
    static final double G = (1 + sqrt(2)); // golden ratio
    static final double T = toRadians(45); // theta
 
    List<Tile> tiles = new ArrayList<>();
 
    public KiteDart() {
        int w =700, h = 450;
        setPreferredSize(new Dimension(w, h));
        setBackground(Color.white);
 
        tiles = deflateTiles(setupPrototiles(w, h), 4
        		); //increase the number to increase the number of times the program expands and generates the tiles
           
        
    
    }
 
    List<Tile> setupPrototiles(int w, int h) {
        List<Tile> proto = new ArrayList<>();
 
        // sun
      // for (double a = PI / 2 + T; a < 415 * PI/180; a += 2 * T)
        //    proto.add(new Tile(Type.Kite, w / 2, h / 2, a, w / 2.5));  
        proto.add(new Tile(Type.Fat, w / 2, h / 2,0 * T , w / 2.5));
 
        return proto;
    }
 
    List<Tile> deflateTiles(List<Tile> tls, int generation) {
        if (generation <= 0)
            return tls;
 
        List<Tile> next = new ArrayList<>();
 
        for (Tile tile : tls) {
            double x = tile.x, y = tile.y, a = tile.angle, nx, ny, mx, my, lx, ly,ox,oy,px,py,qx,qy,rx,ry,sx,sy;
            double size = tile.size / G;
 
            if (tile.type == Type.Thin) {
                next.add(new Tile(Type.Thin, x, y, a + 2 * T, size));
 
                for (int i = 0, sign = 1; i < 2; i++, sign *= -1) {
                    nx = x + cos(a - 3 * T / 2) * tile.size * sign * (G-0.25);
                    ny = y - sin(a - 3 * T / 2) * tile.size * sign * (G-0.25);
                    next.add(new Tile(Type.Thin, nx, ny, a , size));
                    mx = x - cos(a - T / 2) * tile.size * sign * (G - 0.92) ;
                    my = y + sin(a - T / 2) * tile.size * sign * (G - 0.9) ;
                    next.add(new Tile(Type.Fat,mx,my,a + 4 * (i) * T,size));
                    lx = x + sin(a - T / 2) * tile.size * sign * (G - 0.9) ;
                    ly = y + cos(a - T / 2) * tile.size * sign * (G - 0.9) ;
                    next.add(new Tile(Type.Fat,lx,ly,a - sign * Math.pow(3, 1 - i) * T ,size));
                }
 
            } else {
            	next.add(new Tile(Type.Fat, x , y , a - 4 * T , size));
                nx = x - sin(a - T / 2 ) * tile.size * (G - 0.3 );
                ny = y - cos(a - T / 2 ) * tile.size * (G - 0.3 );
                next.add(new Tile(Type.Fat, nx, ny, a - 3 * T , size));
                mx = x - cos(a - 3 * T / 2 ) * tile.size * (G - 0.3 );
                my = y + sin(a - 3 * T / 2 ) * tile.size * (G - 0.3 );
                next.add(new Tile(Type.Fat, mx, my, a + 3 * T , size));
                lx = x + sin(a - 3 * T / 2 ) * tile.size * (G - 0.3 );
                ly = y + cos(a - 3 * T / 2 ) * tile.size * (G - 0.3 );
                next.add(new Tile(Type.Fat, lx, ly, a - 3 * T , size));
                ox = x + cos(a - T / 2 ) * tile.size * (G - 0.3 );
                oy = y - sin(a - T / 2 ) * tile.size * (G - 0.3 );
                next.add(new Tile(Type.Fat, ox, oy, a + 3 * T , size));
                px = x + cos(a + T / 2) * tile.size * 1.52;
                py = y - sin(a + T / 2) * tile.size * 1.52;
                next.add(new Tile(Type.Thin, px, py, a + T , size));
                qx = x + cos(a - 3 * T / 2) * tile.size * 1.52;
                qy = y - sin(a - 3 * T / 2) * tile.size * 1.52;
                next.add(new Tile(Type.Thin, qx, qy, a - 1 * T , size));
                rx = x - sin(a + 3 * T / 2) * tile.size * 1.52;
                ry = y - cos(a + 3 * T / 2) * tile.size * 1.52;
                next.add(new Tile(Type.Thin, rx, ry, a - 2 * T , size));
                sx = x - sin(a + 7 * T / 2) * tile.size * 1.52;
                sy = y - cos(a + 7 * T / 2) * tile.size * 1.52;
                next.add(new Tile(Type.Thin, sx, sy, a , size));
            }
            //next.add(tile);
        }
        // remove duplicates
        tls = next.stream().distinct().collect(toList());
 
        return deflateTiles(tls, generation - 1);
    }
 
    void drawTiles(Graphics2D g) {
    	double[][] dist = {{-G, -2*G*cos(T), -G},{G, 2*G*cos(T/2), G}};
        BufferedImage BIkite=new BufferedImage(1,1,1);
        BufferedImage BIdart=new BufferedImage(1,1,1);
    	try {
    		BIkite = ImageIO.read(this.getClass().getResource("Fat3.png"));
    		BIdart = ImageIO.read(this.getClass().getResource("Thin3.png"));
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
     
        for (Tile tile : tiles) {
        	double angle = tile.angle - 3*T;
            Path2D path = new Path2D.Double();
            path.moveTo(tile.x, tile.y);
      
            int ord = tile.type.ordinal();
            for (int i = 0; i < 3; i++) {
                double x = tile.x + dist[ord][i] * tile.size * cos(angle);
                double y = tile.y - dist[ord][i] * tile.size * sin(angle);
                path.lineTo(x, y);
                angle += T;
            
            }
    /*        
            path.closePath();
            g.setColor(ord == 0 ? Color.orange : Color.yellow);
            g.fill(path);
            g.setColor(Color.darkGray);
            g.draw(path);
      */      
            if(ord==0) {      //kite
            // create the transform, note that the transformations happen
            // in reversed order (so check them backwards)
            AffineTransform at = new AffineTransform();

            // 4. translate it to the center of the component
            at.translate(tile.x  + 0.03*tile.size*Math.cos(tile.angle),tile.y/*-0.82*tile.size*Math.sin(tile.angle)*/);

            // 3. do the actual rotation
            at.rotate(-tile.angle);

            // 2. just a scale because this image is big
            at.scale(tile.size*0.011, tile.size*0.011);
          

            // 1. translate the object so that you rotate it around the 
            //    center (easier :))
            at.translate(-BIkite.getWidth()/2, -BIkite.getHeight()/2);
            
        g.drawImage(BIkite, at ,null);
            }
            else if(ord==1)// Dart
            {
            	
            	// create the transform, note that the transformations happen
                // in reversed order (so check them backwards)
                AffineTransform at = new AffineTransform();

                // 4. translate it to the center of the component
                at.translate(tile.x - 0.05*tile.size*Math.cos(tile.angle),tile.y /*+0.66*tile.size*Math.sin(tile.angle)*/);

                // 3. do the actual rotation
                at.rotate(-tile.angle);

                // 2. just a scale because this image is big
                at.scale(tile.size*0.0113, tile.size*0.0113);
              

                // 1. translate the object so that you rotate it around the 
                //    center (easier :))
                at.translate(-BIdart.getWidth()/2, -BIdart.getHeight()/2);
                
            g.drawImage(BIdart, at ,null);
            	
            	
            }
            /*
             BufferedImage BIkite=new BufferedImage(1,1,1);
        	try {
        		BIkite = ImageIO.read(this.getClass().getResource("Kite.png"));
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
            g.drawImage(BIkite,10,100,null);
            g.drawImage(BIkite,0,0,null);
            */
        }
        
        
        
    }
 

    
    @Override
    public void paintComponent(Graphics og) {
        super.paintComponent(og);
        Graphics2D g = (Graphics2D) og;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(400, 600);//change this number to move image around
        g.scale(0.6,0.6); //change this number to change the size of the image
        drawTiles(g);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Ammann-Beenker Tiling");
            f.setResizable(true);
  
            f.add(new KiteDart(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
        
            f.setVisible(true);
        });
    }
}