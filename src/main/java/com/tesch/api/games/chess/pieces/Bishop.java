package com.tesch.api.games.chess.pieces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.tesch.api.games.chess.ChessPiece;
import com.tesch.api.games.chess.enums.Color;

public class Bishop extends ChessPiece {

    public Bishop(Color color) {
        super(color);
    }
    
    public BufferedImage getAsImage() {
        try {
            BufferedImage img = ImageIO.read(new File("src/main/resources/games/chess/bishop_"+ this.getColor().toString() +".png"));
            return img;
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
