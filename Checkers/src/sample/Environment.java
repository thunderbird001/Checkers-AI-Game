package sample;

import com.sun.javafx.image.PixelAccessor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.effect.Blend;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.print.DocFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Environment {

    Rectangle ar[][] = new Rectangle[8][8];

    Vector < Pair<Integer,Integer> > black = new Vector<>();
    Vector < Pair<Integer,Integer> > white = new Vector<>();
    Vector < Pair<Integer,Integer> > black_king = new Vector<>();
    Vector < Pair<Integer,Integer> > white_king = new Vector<>();

    Map <Pair<Integer , Integer > , Circle > mp = new HashMap<Pair<Integer,Integer> , Circle>();
    Map <Pair<Integer , Integer > , Text > king_text = new HashMap<Pair<Integer,Integer> , Text>();
    Map <Pair<Integer , Integer > , Integer > vis = new HashMap<Pair<Integer,Integer> , Integer>();


    Vector <Circle> pos = new Vector<Circle>();

    AnchorPane root = new AnchorPane();

    int turn = 1;
    int clicked = 0;

    int max_depth = 8;

    int play[] = new int[4];

    int X = -1;
    int Y = -1;


    final int U = 110;

    boolean dist(Circle c, double x, double y)
    {
        double ans = (c.getCenterX() - x)*(c.getCenterX()-x) + (c.getCenterY() - y)*(c.getCenterY()-y);
        ans = Math.sqrt(ans);
        if(ans <= U/2){
            return true;
        }
        return false;
    }

    private void removing_extra(Vector <Pair<Integer,Integer>> white , int x,int y){
        for(int i=0;i<white.size();i++){
            if(white.get(i).getKey() == x && white.get(i).getValue() == y){
                root.getChildren().remove(mp.get(white.get(i)));
                if(king_text.containsKey(white.get(i))){
                    root.getChildren().remove(king_text.get(white.get(i)));
                    king_text.remove(white.get(i));
                }
                mp.remove(white.get(i));
                white.remove(i);
                return;
            }
        }
    }

    private void make_king(Vector<Pair<Integer,Integer>> black, Vector<Pair<Integer,Integer>> black_king , int x,int y)
    {
        for(int i=0;i<black.size();i++){
            if(black.get(i).getKey() == x && black.get(i).getValue() == y)
            {
                black_king.addElement(black.get(i));

                Text text = new Text();
                text.setText("K");
                text.setFont(Font.font(null, FontWeight.BOLD, 20));
                text.setX(get_x(x,y));
                text.setY(get_y(x,y));
                if(turn == 1)
                text.setFill(Color.WHITE);
                else text.setFill(Color.BLACK);

                king_text.put(black.get(i), text);
                root.getChildren().add(text);

                black.remove(i);
                return;
            }
        }
    }

    void final_step(int x,int y,int x1,int y1)
    {
        boolean flag = false;
        if(turn == 1){
            if(king_text.containsKey(new Pair<Integer,Integer>(x,y)) ==  false) {
                final_step1(x, y, x1, y1, black);
                if(y1 == 0){
                    make_king(black,black_king,x1,y1);
                }
            }
            else final_step1(x,y,x1,y1,black_king);


            if(Math.abs(x - x1) == 2){
                flag = true;
                removing_extra(white , (x+x1)/2,(y+y1)/2);
                removing_extra(white_king , (x+x1)/2,(y+y1)/2);
            }
            if(flag) {
                if (king_text.containsKey(new Pair<Integer, Integer>(x1, y1))) {
                    Vector<Pair<Integer, Integer>> temp = new Vector<>();
                    temp.addElement(new Pair<Integer, Integer>(x1, y1));
                    Vector<Pair<Integer, Integer>> vt = new Vector<Pair<Integer, Integer>>();
                    Vector<Pair<Integer, Integer>> st = new Vector<Pair<Integer, Integer>>();
                    Color cl = Color.BLACK;
                    get_for_black(mp , cl, temp, vt, st);
                    get_for_white(mp , cl, temp, vt, st);
                    if (vt.size() != 0) {
                        turn = 1;
                        clicked = 0;
                        if(play[1] == 1){
                            ai_move(vt,1 , black,white,black_king,white_king);
                        }
                        else{
                            add_color(vt);
                        }
                        return;
                    }
                } else {
                    Vector<Pair<Integer, Integer>> temp = new Vector<>();
                    temp.addElement(new Pair<Integer, Integer>(x1, y1));
                    Vector<Pair<Integer, Integer>> vt = new Vector<Pair<Integer, Integer>>();
                    Vector<Pair<Integer, Integer>> st = new Vector<Pair<Integer, Integer>>();
                    Color cl = Color.BLACK;
                    get_for_black(mp , cl, temp, vt, st);
                    if (vt.size() != 0) {
//                        add_color(vt);
                        turn = 1;
                        clicked = 0;
                        if(play[1] == 1){
                            ai_move(vt,1 , black,white,black_king,white_king);
                        }
                        else{
                            add_color(vt);
                        }
                        return;
                    }
                }
            }
            turn = 2;
            clicked = 0;
            if(play[1] == 1){
//                ai_move(vt,1 , black,white,black_king,white_king);
            }
            else if(play[2] == 1){
                Vector<Pair<Integer,Integer>> emp = new Vector<>();
                ai_move(emp,2 , black,white,black_king,white_king);
            }
            else if(play[3] == 1){
                highlight(2);
            }
        }
        else{
            if(king_text.containsKey(new Pair<Integer,Integer>(x,y)) ==  false) {
                final_step1(x, y, x1, y1, white);
                if(y1 == 7){
                    make_king(white,white_king,x1,y1);
                }
            }
            else final_step1(x,y,x1,y1,white_king);

            if(Math.abs(x - x1) == 2){
                flag = true;
                removing_extra(black , (x+x1)/2,(y+y1)/2);
                removing_extra(black_king , (x+x1)/2,(y+y1)/2);
            }

            if(flag) {
                if (king_text.containsKey(new Pair<Integer, Integer>(x1, y1))) {
                    Vector<Pair<Integer, Integer>> temp = new Vector<>();
                    temp.addElement(new Pair<Integer, Integer>(x1, y1));
                    Vector<Pair<Integer, Integer>> vt = new Vector<Pair<Integer, Integer>>();
                    Vector<Pair<Integer, Integer>> st = new Vector<Pair<Integer, Integer>>();
                    Color cl = Color.WHITE;
                    get_for_black(mp , cl, temp, vt, st);
                    get_for_white(mp , cl, temp, vt, st);
                    if (vt.size() != 0) {
                      turn = 2;
                        clicked = 0;
                        if(play[1] == 1 || play[2] == 1){
                            ai_move(vt,2 , black,white,black_king,white_king);
                        }
                        else{
                            add_color(vt);
                        }
                        return;
                    }
                } else {
                    Vector<Pair<Integer, Integer>> temp = new Vector<>();
                    temp.addElement(new Pair<Integer, Integer>(x1, y1));
                    Vector<Pair<Integer, Integer>> vt = new Vector<Pair<Integer, Integer>>();
                    Vector<Pair<Integer, Integer>> st = new Vector<Pair<Integer, Integer>>();
                    Color cl = Color.WHITE;
                    get_for_white(mp ,cl, temp, vt, st);
                    if (vt.size() != 0) {
                       turn = 2;
                        clicked = 0;
                        if(play[1] == 1 || play[2] == 1){
                            ai_move(vt,2 , black,white,black_king,white_king);
                        }
                        else{
                            add_color(vt);
                        }
                        return;
                    }
                }
            }
            turn = 1;
            clicked = 0;
            if(play[1] == 1){
//                ai_move(vt,1 , black,white,black_king,white_king);
            }
            else if(play[2] == 1){
                highlight(1);
            }
            else if(play[3] == 1){
                highlight(1);
            }
        }
    }

    private void final_step1(int x, int y, int x1, int y1, Vector<Pair<Integer, Integer>> white) {
        for(int i = 0; i< white.size(); i++){
            if(white.get(i).getKey() == x && white.get(i).getValue() == y){

                mp.put(new Pair<Integer, Integer>(x1,y1) , mp.get(white.get(i)));
                mp.remove(white.get(i));
                mp.get(new Pair<Integer,Integer>(x1,y1)).setCenterX(get_x(x1,y1));
                mp.get(new Pair<Integer,Integer>(x1,y1)).setCenterY(get_y(x1,y1));

                if(king_text.containsKey(white.get(i))){
                    king_text.put(new Pair<>(x1, y1) , king_text.get(white.get(i)));
                    king_text.remove(white.get(i));
                    king_text.get(new Pair<Integer,Integer>(x1,y1)).setX(get_x(x1,y1));
                    king_text.get(new Pair<Integer,Integer>(x1,y1)).setY(get_y(x1,y1));
                }

                white.remove(i);
                white.addElement(new Pair<Integer, Integer>(x1,y1));
                break;
            }
        }
    }

    void move_to_pos(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        boolean flag = false;
        int id = -1;
        for(int i=0;i<pos.size();i++){
            if(dist(pos.get(i),x,y)){
                flag = true;
//                System.out.println(x + " " + y);
                id = i;
            }
        }
        if(id != -1){
            clicked = 1;
            int X1 = -1;
            int Y1 = -1;
            for(int i=0;i<8;i++){
                for(int j=0;j<8;j++){
                    if(get_x(i,j) == (int)pos.get(id).getCenterX() && get_y(i,j) == (int)pos.get(id).getCenterY()){
                        X1 = i;
                        Y1 = j;
                    }
                }
            }
            decompose();
            decompose1();
            final_step(X,Y,X1,Y1);
        }
    }

    void decompose1()
    {
        while(pos.size() != 0){
            Circle c = pos.get(0);
            root.getChildren().remove(c);
            pos.remove(c);
        }
    }

    Environment(BorderPane root1 , int p){
        build_board(root1);

        build_pieces();

        for(int i=0;i<4;i++){
            play[i] = 0;
        }

        play[p] = 1;
        root.onMouseClickedProperty().setValue(this::move_to_pos);
    }

    void build_board(BorderPane root1){

        root.setPrefSize(U*8,U*8);
        for(int i=0;i<8;i++) {
            int x = (U * (i + 1) + 370);
            int y = U;
            int w = U;
            for (int j = 0; j < 8; j++) {
                Rectangle r = new Rectangle();
                r.setX(x);
                r.setY(y);
                r.setWidth(w);
                r.setHeight(w);

                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        r.setFill(Color.WHITE);
                    } else r.setFill(Color.RED);
                } else {
                    if (j % 2 == 0) {
                        r.setFill(Color.RED);
                    } else r.setFill(Color.WHITE);
                }
                root.getChildren().add(r);
                ar[i][j] = r;
                y += w;
            }
        }
        root1.setCenter(root);
    }

    int get_x(int x,int y)
    {
        return (int)ar[x][y].getX() + U/2;
    }

    Pair<Integer,Integer> get_from_circle(Circle c)
    {
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(get_x(i,j) == (int)c.getCenterX() && get_y(i,j) == (int)c.getCenterY()){
                    Pair<Integer,Integer> p = new Pair<Integer, Integer>(i,j);
                    return p;
                }
            }
        }
        Pair<Integer,Integer> p = new Pair<Integer, Integer>(0,0);
        return p;
    }

    int get_y(int x,int y)
    {
        return (int)ar[x][y].getY() + U/2;
    }

    void create_circle(int x,int y)
    {
        Circle c = new Circle();
        c.setCenterX(get_x(x,y));
        c.setCenterY(get_y(x,y));
        c.setRadius(U/2 - 10);
        c.setFill(Color.rgb(255,255,0,0.6));
        root.getChildren().add(c);
        pos.addElement(c);
    }

    boolean turn_positions_for_black(int x,int y,Color cl)
    {
        int x1 = x+1;
        int y1 = y-1;
        int x2 = x-1;
        int y2 = y-1;
        boolean flag = false;
        if(check(x1,y1)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == true && mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                if (check(x1 + 1, y1 - 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x1 + 1, y1 - 1)) == false) {
                        flag = true;
                        create_circle(x1+1,y1-1);
                    }
                }
            }
        }

        if(check(x2,y2)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == true && mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                if (check(x2 - 1, y2 - 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x2 - 1, y2 - 1)) == false) {
                        flag = true;
                        create_circle(x2-1,y2-1);
                    }
                }
            }
        }
        if(flag == false){
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                    create_circle(x2, y2);
                }
            }
            if(check(x1,y1)){
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                    create_circle(x1, y1);
                }
            }
        }
        return flag;
    }

    boolean turn_positions_for_black1(Vector<Pair<Integer,Integer>> temp ,int x,int y,Color cl , Map <Pair<Integer , Integer > , Circle > mp)
    {
        int x1 = x+1;
        int y1 = y-1;
        int x2 = x-1;
        int y2 = y-1;
        boolean flag = false;
        if(check(x1,y1)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == true && mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                if (check(x1 + 1, y1 - 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x1 + 1, y1 - 1)) == false) {
                        flag = true;
//                        create_circle(x1+1,y1-1);
                        temp.addElement(new Pair<>(x1+1,y1-1));
                    }
                }
            }
        }

        if(check(x2,y2)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == true && mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                if (check(x2 - 1, y2 - 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x2 - 1, y2 - 1)) == false) {
                        flag = true;
//                        create_circle(x2-1,y2-1);
                        temp.addElement(new Pair<>(x2-1,y2-1));
                    }
                }
            }
        }
        if(flag == false){
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
//                    create_circle(x2, y2);
                    temp.addElement(new Pair<>(x2,y2));
                }
            }
            if(check(x1,y1)){
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
//                    create_circle(x1, y1);
                    temp.addElement(new Pair<>(x1,y1));
                }
            }
        }
        return flag;
    }

    boolean turn_positions_for_white(int x,int y,Color cl)
    {
        int x1 = x+1;
        int y1 = y+1;
        int x2 = x-1;
        int y2 = y+1;
        boolean flag = false;
        if(check(x1,y1)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == true && mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                if (check(x1 + 1, y1 + 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x1 + 1, y1 + 1)) == false) {
                        flag = true;
                        create_circle(x1+1,y1+1);
                    }
                }
            }
        }

        if(check(x2,y2)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == true && mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                if (check(x2 - 1, y2 + 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x2 - 1, y2 + 1)) == false) {
                        flag = true;
                        create_circle(x2-1,y2+1);
                    }
                }
            }
        }
        if(flag == false){
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                    create_circle(x2, y2);
                }
            }
            if(check(x1,y1)){
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                    create_circle(x1, y1);
                }
            }
        }
        return flag;
    }

    boolean turn_positions_for_white1(Vector<Pair<Integer,Integer>> temp ,int x,int y,Color cl , Map <Pair<Integer , Integer > , Circle > mp)
    {
        int x1 = x+1;
        int y1 = y+1;
        int x2 = x-1;
        int y2 = y+1;
        boolean flag = false;
        if(check(x1,y1)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == true && mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                if (check(x1 + 1, y1 + 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x1 + 1, y1 + 1)) == false) {
                        flag = true;
//                        create_circle(x1+1,y1+1);
                        temp.addElement(new Pair<>(x1+1,y1+1));
                    }
                }
            }
        }

        if(check(x2,y2)) {
            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == true && mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                if (check(x2 - 1, y2 + 1)) {
                    if (mp.containsKey(new Pair<Integer, Integer>(x2 - 1, y2 + 1)) == false) {
                        flag = true;
//                        create_circle(x2-1,y2+1);
                        temp.addElement(new Pair<>(x2-1,y2+1));
                    }
                }
            }
        }
        if(flag == false){
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
//                    create_circle(x2, y2);
                    temp.addElement(new Pair<>(x2,y2));
                }
            }
            if(check(x1,y1)){
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
//                    create_circle(x1, y1);
                    temp.addElement(new Pair<>(x1,y1));
                }
            }
        }
        return flag;
    }

    void build_pieces(){
        for(int i=0;i<8;i++){


            for(int j=0;j<8;j++){
                if(j == 3 || j==4 ) continue;
                if(i%2 == j%2 ) continue;

                DropShadow dropShadow = new DropShadow();
                dropShadow.setOffsetX(6);
                dropShadow.setOffsetY(4);


                Circle circle = new Circle();
                circle.setCenterX(ar[i][j].getX()+U/2);
                circle.setCenterY(ar[i][j].getY()+U/2);
                circle.setRadius(U/2-10);
                circle.setEffect(dropShadow);

                Pair <Integer,Integer> p = new Pair<Integer, Integer>(i,j);

                if(j<3){
                    circle.setFill(Color.WHITE);
                    white.addElement(p);
                }
                else {
                    circle.setFill(Color.BLACK);
                    dropShadow.colorProperty().setValue(Color.WHITE);
                    black.addElement(p);
                }
                mp.put(p,circle);
                root.getChildren().add(circle);

                circle.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(clicked == 0){
//                            System.out.println("clicked");
                            decompose1();
                            Pair<Integer,Integer> p1 = get_from_circle(circle);
                            if(turn == 1 && circle.getFill() == Color.BLACK && vis.containsKey(p1) == true ){
                                int x = p1.getKey();
                                int y = p1.getValue();
                                X = x;
                                Y = y;
                                Color cl = Color.BLACK;
                                boolean flag1 = turn_positions_for_black(x,y,cl);
                                if(king_text.containsKey(p1) == true){
                                    boolean flag2 = turn_positions_for_white(x,y,cl);
                                    if(flag1 != flag2){
                                        decompose1();
                                        if(flag1){
                                            turn_positions_for_black(x,y,cl);
                                        }
                                        else turn_positions_for_white(x,y,cl);
                                    }
                                }
                            }
                            else if(turn == 2 && circle.getFill() == Color.WHITE && vis.containsKey(p1) == true){
                                int x = p1.getKey();
                                int y = p1.getValue();
                                X = x;
                                Y = y;
                                Color cl = Color.WHITE;
                                boolean flag1 = turn_positions_for_white(x,y,cl);
                                if(king_text.containsKey(p1) == true){
                                    boolean flag2 = turn_positions_for_black(x,y,cl);
                                    if(flag1 != flag2){
                                        decompose1();
                                        if(flag1){
                                            turn_positions_for_white(x,y,cl);
                                        }
                                        else turn_positions_for_black(x,y,cl);
                                    }
                                }
                            }
                        }
                    }
                });


            }
        }
    }

    boolean check(int x,int y)
    {
        return x>=0 && x<8 && y>=0 && y<8;
    }

    void decompose()
    {
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        ar[i][j].setFill(Color.WHITE);
                    } else ar[i][j].setFill(Color.RED);
                } else {
                    if (j % 2 == 0) {
                        ar[i][j].setFill(Color.RED);
                    } else ar[i][j].setFill(Color.WHITE);
                }
            }
        }
    }

    private boolean get_for_black(Map <Pair<Integer , Integer > , Circle > mp , Color cl , Vector<Pair<Integer,Integer>> black,Vector <Pair<Integer , Integer>> vt,Vector <Pair<Integer , Integer>> st)
    {
        for(int i=0;i<black.size();i++){
            int x = black.get(i).getKey();
            int y = black.get(i).getValue();
            int x1 = x+1;
            int y1 = y-1;
            int x2 = x-1;
            int y2 = y-1;
            boolean f = false;
            if(check(x1,y1)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                    st.addElement(black.get(i));
                    f = true;
                } else {
                    if (mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                        x1++;
                        y1--;
                        if(check(x1,y1)){
                            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                                vt.addElement(black.get(i));
                            }
                        }
                    }
                }
            }
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                    if (f == false)
                        st.addElement(black.get(i));
                } else {
                    if (mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                        x2--;
                        y2--;
                        if(check(x2,y2)){
                            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                                vt.addElement(black.get(i));
                            }
                        }
                    }
                }
            }
        }
        if(vt.size() == 0 && st.size() == 0){
            return false;
        }
        return true;
    }

    private boolean get_for_white(Map <Pair<Integer , Integer > , Circle > mp , Color cl , Vector<Pair<Integer,Integer>> white,Vector <Pair<Integer , Integer>> vt,Vector <Pair<Integer , Integer>> st)
    {
        for(int i=0;i<white.size();i++){
            int x = white.get(i).getKey();
            int y = white.get(i).getValue();
            int x1 = x+1;
            int y1 = y+1;
            int x2 = x-1;
            int y2 = y+1;
            boolean f = false;
            if(check(x1,y1)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                    st.addElement(white.get(i));
                    f = true;
                } else {
                    if (mp.get(new Pair<Integer, Integer>(x1, y1)).getFill() != cl) {
                        x1++;
                        y1++;
                        if(check(x1,y1)){
                            if (mp.containsKey(new Pair<Integer, Integer>(x1, y1)) == false) {
                                vt.addElement(white.get(i));
                            }
                        }
                    }
                }
            }
            if(check(x2,y2)) {
                if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                    if (f == false)
                        st.addElement(white.get(i));
                } else {
                    if (mp.get(new Pair<Integer, Integer>(x2, y2)).getFill() != cl) {
                        x2--;
                        y2++;
                        if(check(x2,y2)){
                            if (mp.containsKey(new Pair<Integer, Integer>(x2, y2)) == false) {
                                vt.addElement(white.get(i));
                            }
                        }
                    }
                }
            }
        }
        if(vt.size() == 0 && st.size() == 0){
            return false;
        }
        return true;
    }

    void highlight(int a)
    {
        vis.clear();
        Vector <Pair<Integer , Integer>> vt = new Vector<Pair<Integer,Integer> >();
        Vector <Pair<Integer , Integer>> st = new Vector<Pair<Integer,Integer> >();


        if(a == 1){
            Color cl = Color.BLACK;
            get_for_black(mp , cl , black_king,vt,st);
            get_for_white(mp  ,cl , black_king,vt,st);
            get_for_black(mp , cl , black,vt,st);
        }
        else{
            Color cl = Color.WHITE;
            get_for_black(mp , cl , white_king,vt,st);
            get_for_white(mp , cl , white_king,vt,st);
            get_for_white(mp , cl , white,vt,st);
        }
        if(vt.size() != 0){
            add_color(vt);
        }
        else{
            add_color(st);
        }
        if(vt.size() == 0 && st.size() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if(turn == 1)
            alert.setContentText("WHITE WINS");
            else
                alert.setContentText("BLACK WINS");
            alert.show();
        }
    }

    private void add_color(Vector<Pair<Integer, Integer>> st) {
        for(int i=0;i<st.size();i++){
            int x = st.get(i).getKey();
            int y = st.get(i).getValue();
            ar[x][y].setFill(Color.LAWNGREEN);
            vis.put(st.get(i),1);
        }
    }

    int calculate_heuristic(Vector<Pair<Integer,Integer>> bl , Vector<Pair<Integer,Integer>> wt , Vector<Pair<Integer,Integer>> bl_king , Vector<Pair<Integer,Integer>> wt_king)
    {
        int score = 0;
        for(int i=0;i<bl.size();i++){
            if(bl.get(i).getValue() < 4){
                score += 7;
            }
            else score += 5;
        }
        for(int i=0;i<wt.size();i++){
            if(wt.get(i).getValue() > 3){
                score -= 7;
            }
            else score -= 5;
        }
        score += bl_king.size()*10;
        score -= wt_king.size()*10;
//        System.out.println("score is: " + score);
        return score;
    }

    boolean is_king(int x, int y, Vector <Pair<Integer,Integer>> bl_king)
    {
        for(int i=0;i<bl_king.size();i++){
            if(bl_king.get(i).getKey() == x && bl_king.get(i).getValue() == y){
                return true;
            }
        }
        return false;
    }

    void move_for_ai(Vector<Pair<Integer,Integer>> bl , int x,int y,int x1,int y1,Map <Pair<Integer , Integer > , Circle > vp)
    {
        for(int i=0;i<bl.size();i++){
            if(bl.get(i).getKey() == x && bl.get(i).getValue() == y)
            {
                vp.put(new Pair<>(x1,y1) , vp.get(bl.get(i)));
                vp.remove(bl.get(i));
                bl.addElement(new Pair<>(x1,y1));
                bl.remove(i);
                break;
            }
        }
    }

    void make_king_for_ai(Vector<Pair<Integer,Integer>> bl , Vector<Pair<Integer,Integer>> bl_king , int x,int y)
    {
        for(int i=0;i<bl.size();i++){
            if(bl.get(i).getKey() == x && bl.get(i).getValue() == y)
            {
                bl_king.addElement(bl.get(i));
                bl.remove(i);
                break;
            }
        }
    }

    void removing_extra_for_ai(Vector<Pair<Integer,Integer>> wt , int x,int y,Map <Pair<Integer , Integer > , Circle > vp)
    {
        for(int i=0;i<wt.size();i++){
            if(wt.get(i).getKey() == x && wt.get(i).getValue() == y)
            {
                vp.remove(wt.get(i));
                wt.remove(i);
                break;
            }
        }
    }

    Node max_player(Vector <Pair<Integer,Integer>> pre , Vector<Pair<Integer,Integer>> bl , Vector<Pair<Integer,Integer>> wt, Vector<Pair<Integer,Integer>> bl_king , Vector<Pair<Integer,Integer>> wt_king ,int alpha, int beta, int depth, Map <Pair<Integer , Integer > , Circle > vp)
    {
        Node obj = new Node();
        obj.value = -150;

        Vector <Pair<Integer,Integer>> vt = new Vector<>();
        Vector <Pair<Integer,Integer>> st = new Vector<>();

        Color cl = Color.BLACK;
        get_for_black(vp , cl , bl_king,vt,st);
        get_for_white(vp  ,cl , bl_king,vt,st);
        get_for_black(vp , cl , bl,vt,st);

        if(vt.size() == 0 && st.size() == 0){
            obj.value = -150;
            return obj;
        }

//        System.out.println("depth is: " + depth +" in black");
        if(depth == max_depth){
//            System.out.println("hello");
            obj.value = calculate_heuristic(bl,wt,bl_king,wt_king);
            return obj;
        }
        if(pre.size() != 0){
            vt = pre;
        }
        Vector <Pair<Integer, Integer>> emp = new Vector<>();

        if(vt.size() != 0){
            for(int i=0;i<vt.size();i++) {
                int x = vt.get(i).getKey();
                int y = vt.get(i).getValue();
//                System.out.println("in vt " + x + " " + y);
                Vector <Pair<Integer,Integer>> temp = new Vector<>();
                boolean flag = turn_positions_for_black1(temp,x,y,Color.BLACK,vp);
                boolean is_it = false;
                if(is_king(x,y,bl_king)){
                    is_it = true;
                    boolean flag1 = turn_positions_for_white1(temp,x,y,Color.BLACK,vp);
                    if(flag != flag1){
                        temp.clear();
                        if(flag) turn_positions_for_black1(temp,x,y,Color.BLACK,vp);
                        else turn_positions_for_white1(temp,x,y,Color.BLACK,vp);
                    }
                }

                for(int j=0;j<temp.size();j++){
                    int x1 = temp.get(j).getKey();
                    int y1 = temp.get(j).getValue();
//                    System.out.println("hello in temp " + x1 + " " + y1);
                    Vector <Pair<Integer,Integer>> bl1 = (Vector<Pair<Integer, Integer>>) bl.clone();
                    Vector <Pair<Integer,Integer>> wt1 = (Vector<Pair<Integer, Integer>>) wt.clone();
                    Vector <Pair<Integer,Integer>> bl_king1 = (Vector<Pair<Integer, Integer>>) bl_king.clone();
                    Vector <Pair<Integer,Integer>> wt_king1 = (Vector<Pair<Integer, Integer>>) wt_king.clone();

                    Map <Pair<Integer , Integer > , Circle > vp1 = new HashMap<Pair<Integer,Integer> , Circle>();
                    make_map(bl1,wt1,bl_king1,wt_king1,vp1);

                    if(is_it == true){
                        move_for_ai(bl_king1,x,y,x1,y1,vp1);
                    }
                    else{
                        move_for_ai(bl1,x,y,x1,y1,vp1);
                        if(y1 == 0){
                            is_it = true;
                            make_king_for_ai(bl1,bl_king1,x1,y1);
                        }
                    }
                    if(Math.abs(x - x1) == 2){
                        removing_extra_for_ai(wt1,(x+x1)/2,(y+y1)/2,vp1);
                        removing_extra_for_ai(wt_king1,(x+x1)/2,(y+y1)/2,vp1);
                    }
                    Vector <Pair<Integer,Integer>> vt1 = new Vector<>();
                    Vector <Pair<Integer,Integer>> st1 = new Vector<>();
                    Vector <Pair<Integer,Integer>> res = new Vector<>();
                    res.addElement(temp.get(j));
                    if(is_it){
                        get_for_black(vp1,cl,res,vt1,st1);
                        get_for_white(vp1,cl,res,vt1,st1);
                    }
                    else{
                        get_for_black(vp1,cl,res,vt1,st1);
                    }
                    Node node = new Node();
                    if(vt1.size() != 0){
                        node = max_player(res ,bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
                    }
                    else{
                        node = min_player(emp , bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
                    }
//                    System.out.println("Node value is: " + node.value);
                    if(obj.value < node.value){
                        obj = node;
                        obj.x = x;
                        obj.y = y;
                        obj.x1 = x1;
                        obj.y1 = y1;
//                        System.out.println("values "+ x + " " + x1 + " " + x1 + " "+ y1);
                    }
                    alpha = Math.max(alpha , obj.value);
                    if(beta <= alpha){
                        return obj;
                    }
                }
            }
        }
        else{
            for(int i=0;i<st.size();i++) {
                int x = st.get(i).getKey();
                int y = st.get(i).getValue();
//                System.out.println("int st:" + x + " "+ y);
                Vector <Pair<Integer,Integer>> temp = new Vector<>();
                boolean flag = turn_positions_for_black1(temp,x,y,Color.BLACK,vp);
//                System.out.println(temp.size());
                boolean is_it = false;
                if(is_king(x,y,bl_king)){
                    is_it = true;
                    boolean flag1 = turn_positions_for_white1(temp,x,y,Color.BLACK,vp);
                    if(flag != flag1){
                        temp.clear();
                        if(flag) turn_positions_for_black1(temp,x,y,Color.BLACK,vp);
                        else turn_positions_for_white1(temp,x,y,Color.BLACK,vp);
                    }
                }

                for(int j=0;j<temp.size();j++){
//                    System.out.println("hello in temp");
                    int x1 = temp.get(j).getKey();
                    int y1 = temp.get(j).getValue();
//                    System.out.println(x1 + " " + y1);
                    Vector <Pair<Integer,Integer>> bl1 = (Vector<Pair<Integer, Integer>>) bl.clone();
                    Vector <Pair<Integer,Integer>> wt1 = (Vector<Pair<Integer, Integer>>) wt.clone();
                    Vector <Pair<Integer,Integer>> bl_king1 = (Vector<Pair<Integer, Integer>>) bl_king.clone();
                    Vector <Pair<Integer,Integer>> wt_king1 = (Vector<Pair<Integer, Integer>>) wt_king.clone();

                    Map <Pair<Integer , Integer > , Circle > vp1 = new HashMap<Pair<Integer,Integer> , Circle>();
                    make_map(bl1,wt1,bl_king1,wt_king1,vp1);

                    if(is_it == true){
                        move_for_ai(bl_king1,x,y,x1,y1,vp1);
                    }
                    else{
                        move_for_ai(bl1,x,y,x1,y1,vp1);
                        if(y1 == 0){
                            is_it = true;
                            make_king_for_ai(bl1,bl_king1,x1,y1);
                        }
                    }

                    Node node = new Node();
                    node = min_player(emp , bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
//                    System.out.println("Node value is: " + node.value);
                    if(obj.value < node.value){
                        obj = node;
                        obj.x = x;
                        obj.y = y;
                        obj.x1 = x1;
                        obj.y1 = y1;
//                        System.out.println("values "+ x + " " + y + " " + x1 + " "+ y1);
                    }
                    alpha = Math.max(alpha , obj.value);
                    if(beta <= alpha){
                        return obj;
                    }
                }
            }
        }

        return obj;
    }

    Node min_player(Vector <Pair<Integer,Integer>> pre , Vector<Pair<Integer,Integer>> bl , Vector<Pair<Integer,Integer>> wt, Vector<Pair<Integer,Integer>> bl_king , Vector<Pair<Integer,Integer>> wt_king ,int alpha, int beta, int depth, Map <Pair<Integer , Integer > , Circle > vp)
    {
        Node obj = new Node();
        obj.value = 150;

        Vector <Pair<Integer,Integer>> vt = new Vector<>();
        Vector <Pair<Integer,Integer>> st = new Vector<>();

        Color cl = Color.WHITE;
        get_for_black(vp , cl , wt_king,vt,st);
        get_for_white(vp  ,cl , wt_king,vt,st);
        get_for_white(vp , cl , wt,vt,st);

        if(vt.size() == 0 && st.size() == 0){
            obj.value = 150;
            return obj;
        }

//        System.out.println("depth is: " + depth +" in white");
        if(depth == max_depth){
//            System.out.println("hello");
            obj.value = calculate_heuristic(bl,wt,bl_king,wt_king);
            return obj;
        }

        if(pre.size() != 0){
            vt = pre;
        }
        Vector <Pair<Integer,Integer>> emp = new Vector<>();

        if(vt.size() != 0){
            for(int i=0;i<vt.size();i++) {
                int x = vt.get(i).getKey();
                int y = vt.get(i).getValue();
//                System.out.println("int vt " + x + " " + y );
                Vector <Pair<Integer,Integer>> temp = new Vector<>();
                boolean flag = turn_positions_for_white1(temp,x,y,Color.WHITE,vp);
                boolean is_it = false;
                if(is_king(x,y,wt_king)){
                    is_it = true;
                    boolean flag1 = turn_positions_for_black1(temp,x,y,Color.WHITE,vp);
                    if(flag != flag1){
                        temp.clear();
                        if(flag) turn_positions_for_white1(temp,x,y,Color.WHITE,vp);
                        else turn_positions_for_black1(temp,x,y,Color.WHITE,vp);
                    }
                }

                for(int j=0;j<temp.size();j++){
                    int x1 = temp.get(j).getKey();
                    int y1 = temp.get(j).getValue();
//                    System.out.println("hello in temp " + x1 + " " + y1);
                    Vector <Pair<Integer,Integer>> bl1 = (Vector<Pair<Integer, Integer>>) bl.clone();
                    Vector <Pair<Integer,Integer>> wt1 = (Vector<Pair<Integer, Integer>>) wt.clone();
                    Vector <Pair<Integer,Integer>> bl_king1 = (Vector<Pair<Integer, Integer>>) bl_king.clone();
                    Vector <Pair<Integer,Integer>> wt_king1 = (Vector<Pair<Integer, Integer>>) wt_king.clone();

                    Map <Pair<Integer , Integer > , Circle > vp1 = new HashMap<Pair<Integer,Integer> , Circle>();
                    make_map(bl1,wt1,bl_king1,wt_king1,vp1);

                    if(is_it == true){
                        move_for_ai(wt_king1,x,y,x1,y1,vp1);
                    }
                    else{
                        move_for_ai(wt1,x,y,x1,y1,vp1);
                        if(y1 == 7){
                            is_it = true;
                            make_king_for_ai(wt1,wt_king1,x1,y1);
                        }
                    }
                    if(Math.abs(x - x1) == 2){
                        removing_extra_for_ai(bl1,(x+x1)/2,(y+y1)/2,vp1);
                        removing_extra_for_ai(bl_king1,(x+x1)/2,(y+y1)/2,vp1);
                    }
                    Vector <Pair<Integer,Integer>> vt1 = new Vector<>();
                    Vector <Pair<Integer,Integer>> st1 = new Vector<>();
                    Vector <Pair<Integer,Integer>> res = new Vector<>();
                    res.addElement(temp.get(j));
                    if(is_it){
                        get_for_black(vp1,cl,res,vt1,st1);
                        get_for_white(vp1,cl,res,vt1,st1);
                    }
                    else{
                        get_for_white(vp1,cl,res,vt1,st1);
                    }
                    Node node = new Node();
                    if(vt1.size() != 0){
                        node = min_player(res , bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
                    }
                    else{
                        node = max_player(emp , bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
                    }
//                    System.out.println("Node value is: " + node.value);
                    if(obj.value > node.value){
                        obj = node;
                        obj.x = x;
                        obj.y = y;
                        obj.x1 = x1;
                        obj.y1 = y1;
//                        System.out.println("values "+ x + " " + x1 + " " + x1 + " "+ y1);
                    }
                    beta = Math.min(beta , obj.value);
                    if(beta <= alpha){
                        return obj;
                    }
                }
            }
        }
        else{
            for(int i=0;i<st.size();i++) {
                int x = st.get(i).getKey();
                int y = st.get(i).getValue();
//                System.out.println("int st:" + x + " "+ y);
                Vector <Pair<Integer,Integer>> temp = new Vector<>();
                boolean flag = turn_positions_for_white1(temp,x,y,Color.WHITE,vp);
                boolean is_it = false;
                if(is_king(x,y,wt_king)){
                    is_it = true;
                    boolean flag1 = turn_positions_for_black1(temp,x,y,Color.WHITE,vp);
                    if(flag != flag1){
                        temp.clear();
                        if(flag) turn_positions_for_white1(temp,x,y,Color.WHITE,vp);
                        else turn_positions_for_black1(temp,x,y,Color.WHITE,vp);
                    }
                }

                for(int j=0;j<temp.size();j++){
//                    System.out.println("hello in temp");
                    int x1 = temp.get(j).getKey();
                    int y1 = temp.get(j).getValue();
//                    System.out.println(x1 + " " + y1);
                    Vector <Pair<Integer,Integer>> bl1 = (Vector<Pair<Integer, Integer>>) bl.clone();
                    Vector <Pair<Integer,Integer>> wt1 = (Vector<Pair<Integer, Integer>>) wt.clone();
                    Vector <Pair<Integer,Integer>> bl_king1 = (Vector<Pair<Integer, Integer>>) bl_king.clone();
                    Vector <Pair<Integer,Integer>> wt_king1 = (Vector<Pair<Integer, Integer>>) wt_king.clone();

                    Map <Pair<Integer , Integer > , Circle > vp1 = new HashMap<Pair<Integer,Integer> , Circle>();
                    make_map(bl1,wt1,bl_king1,wt_king1,vp1);

                    if(is_it == true){
                        move_for_ai(wt_king1,x,y,x1,y1,vp1);
                    }
                    else{
                        move_for_ai(wt1,x,y,x1,y1,vp1);
                        if(y1 == 0){
                            is_it = true;
                            make_king_for_ai(wt1,wt_king1,x1,y1);
                        }
                    }

                    Node node = new Node();
                    node = max_player(emp , bl1,wt1,bl_king1,wt_king1,alpha,beta,depth+1,vp1);
//                    System.out.println("Node value is: " + node.value);
                    if(obj.value > node.value){
                        obj = node;
                        obj.x = x;
                        obj.y = y;
                        obj.x1 = x1;
                        obj.y1 = y1;
//                        System.out.println("values "+ x + " " + y + " " + x1 + " "+ y1);
                    }
                    beta = Math.min(beta , obj.value);
                    if(beta <= alpha){
                        return obj;
                    }
                }
            }
        }

        return obj;
    }

    void ai_move(Vector <Pair<Integer,Integer>> pre ,int k , Vector<Pair<Integer,Integer>> black , Vector<Pair<Integer,Integer>> white , Vector<Pair<Integer,Integer>> black_king , Vector<Pair<Integer,Integer>> white_king)
    {
        Vector <Pair<Integer,Integer>> bl = (Vector<Pair<Integer, Integer>>) black.clone();
        Vector <Pair<Integer,Integer>> bl_king = (Vector<Pair<Integer, Integer>>) black_king.clone();
        Vector <Pair<Integer,Integer>> wt = (Vector<Pair<Integer, Integer>>) white.clone();
        Vector <Pair<Integer,Integer>> wt_king = (Vector<Pair<Integer, Integer>>) white_king.clone();
        Map <Pair<Integer , Integer > , Circle > vp = new HashMap<Pair<Integer,Integer> , Circle>();

        make_map(bl,wt,bl_king,wt_king,vp);
        Node obj = new Node();
//        System.out.println("pre size is : " + pre.size());
        if(k == 1)
        obj = max_player(pre , bl,wt,bl_king,wt_king,-150,150,0,vp);
        else
            obj = min_player(pre , bl,wt,bl_king,wt_king,-150,150,0,vp);
//        System.out.println(obj.x +" " + obj.y + " " + obj.x1 + " " + obj.y1);
//        System.out.println(obj.value);
        final_step(obj.x , obj.y , obj.x1 , obj.y1);
    }

    void make_map(Vector<Pair<Integer,Integer>> bl, Vector<Pair<Integer,Integer>> wt , Vector<Pair<Integer,Integer>> bl_king , Vector<Pair<Integer,Integer>> wt_king,Map <Pair<Integer , Integer > , Circle > vp )
    {
        making_map(Color.BLACK , bl , vp);
        making_map(Color.BLACK , bl_king , vp);
        making_map(Color.WHITE , wt , vp);
        making_map(Color.WHITE , wt_king , vp);
    }

    void making_map(Color cl , Vector<Pair<Integer,Integer>> bl , Map <Pair<Integer , Integer > , Circle > vp)
    {
        for(int i=0;i<bl.size();i++){
            int x = bl.get(i).getKey();
            int y = bl.get(i).getValue();
            Circle circle = new Circle();
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setFill(cl);
            vp.put(bl.get(i) , circle);
        }
    }

}
