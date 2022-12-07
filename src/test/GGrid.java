package test;

import java.awt.*;

import javax.swing.*;

public class GGrid extends JFrame {

    public GGrid() {

        setSize( 1000, 1000 );

        setVisible( true );

    }

    public void paint( Graphics g )

    {

        for ( int x = 100; x <= 700; x += 120 )

            for ( int y = 100; y <= 700; y += 120 )

                g.drawRect( x, y, 120, 120 );

    }

    public static void main( String args[] )

    {

        GGrid application = new GGrid();

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); }
}


