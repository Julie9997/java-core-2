package ru.geekbrains.lesson2;

import java.util.Random;
import java.util.Scanner;

public class Program {

    private static final char DOT_HUMAN = 'X'; // Фишка игрока - человека
    private static final char DOT_AI = '0'; // Фишка игрока - компьютер
    private static final char DOT_EMPTY = '*'; // Признак пустого поля
    private static final Scanner scanner = new Scanner(System.in);
    private static  final Random random = new Random();
    private static char[][] field; // Двумерный массив хранит состояние игрового поля
    private static int fieldSizeX; // Размерность игрового поля
    private static int fieldSizeY; // Размерность игрового поля
    private static int WIN_COUNT; // Кол-во фишек для победы

    public static void main(String[] args) {
        while (true){
            initialize();
            printField();
            while (true){
                humanTurn();
                printField();
                if (gameCheck(DOT_HUMAN, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(DOT_AI, "Победил компьютер!"))
                    break;
            }
            System.out.print("Желаете сыграть еще раз? (Y - да): ");
            if (!scanner.next().equalsIgnoreCase("Y"))
                break;
        }
    }

    /**
     * Инициализация начального состояния игры
     */
    private static void initialize(){
        System.out.println("Выберите размеры игрового поля. Введите N для поля NxN: ");
        int N = scanner.nextInt();
        fieldSizeX = N;
        fieldSizeY = N;

        if (N == 3){
            System.out.println("Классические крестики-нолики");
            WIN_COUNT = 3;
        } else {
            WIN_COUNT = N - 1;
            System.out.println("Для победы нужно составить ряд из " + WIN_COUNT + " элементов");
        }

        field = new char[fieldSizeY][fieldSizeX];
        for (int y = 0; y < fieldSizeY; y++){
            for (int x = 0; x < fieldSizeX; x++){
                field[y][x] = DOT_EMPTY;
            }
        }
    }

    /**
     * Отрисовать текущее состояние игрового поля
     */
    private static void printField(){
        System.out.print("+");
        for (int i = 0; i < fieldSizeX*2 + 1; i++){
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeY; i++){
            System.out.print(i + 1 + "|");
            for (int j = 0; j < fieldSizeX; j++){
                System.out.print(field[i][j] + "|");
            }
            System.out.println();
        }

        for (int i = 0; i < fieldSizeX*2 + 2; i++){
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Обработка хода игрока (человека)
     */
    private static void humanTurn(){
        int x, y;
        do{
            System.out.print("Укажите координаты хода X и Y (от 1 до "
                + fieldSizeX + " )\nчерез пробел: ");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        }
        while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = DOT_HUMAN;
    }

    /**
     * Обработка хода компьютера
     */
    static void aiTurn(){
        int x, y;

        // поиск победного хода
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isCellEmpty(i, j)) {
                    field[i][j] = DOT_AI;
                    if (checkWin(DOT_AI)) {
                        return;
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }

        // Если нет победного хода, попытка заблокировать победный ход игрока
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isCellEmpty(i, j)) {
                    field[i][j] = DOT_HUMAN;
                    if (checkWin(DOT_HUMAN)) {
                        field[i][j] = DOT_AI;
                        return;
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }

        // если нет выигрышного хода
        do{
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        }
        while (!isCellEmpty(x, y));
        field[x][y] = DOT_AI;
    }

    /**
     * Проверка, ячейка является пустой (DOT_EMPTY)
     * @param x
     * @param y
     * @return
     */
    static boolean isCellEmpty(int x, int y){
        return field[x][y] == DOT_EMPTY;
    }

    /**
     * Проверка состояния игры
     * @param dot фишка игрока
     * @param winStr победный слоган
     * @return признак продолжения игры (true - завершение игры)
     */
    static boolean gameCheck(char dot, String winStr){
        if (checkWin(dot)){
            System.out.println(winStr);
            return true;
        }
        if (checkDraw()){
            System.out.println("Ничья!");
            return true;
        }
        return false; // Продолжим игру
    }

    /**
     * Проверка корректности ввода
     * @param x
     * @param y
     * @return
     */
    static boolean isCellValid(int x, int y){
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Проверка победы
     * @param c фишка игрока (X или 0)
     * @return
     */
    static boolean checkWin(char c){

        return checkHorizonts(c) || checkDiagonals(c) || checkVerticals(c);
    }

    /**
     * Проверяет горизонтали поля на наличие выигрышных комбинаций
     * @param dot фишка игрока
     * @return
     */
    static boolean checkHorizonts(char dot) {
        for (int i = 0; i < fieldSizeX; i++) {
            int count = 0;
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[i][j] == dot) {
                    count++;
                    if (count == WIN_COUNT) {
                        return true;
                    }
                } else {
                    count = 0; // Сброс счетчика если ряд прерывается
                }
            }
        }
        return false;
    }


    /**
     * Проверяет вертикали поля на наличие выигрышных комбинаций
     * @param dot фишка игрока
     * @return
     */
    static boolean checkVerticals(char dot) {
        for (int i = 0; i < fieldSizeX; i++) {
            int count = 0;
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[j][i] == dot) {
                    count++;
                    if (count == WIN_COUNT) {
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }

    /**
     * Проверяет диагонали поля на наличие выигрышных комбинаций
     * @param dot фишка игрока
     * @return
     */
    static boolean checkDiagonals(char dot) {
        for (int i = 0; i < fieldSizeX; i++) {
            int count = 0;
            int reverseCount = 0;
            for (int j = 0; j < fieldSizeY; j++) {
                if (i + j < fieldSizeX) {
                    if (field[i + j][j] == dot) {
                        count++;
                        if (count == WIN_COUNT) {
                            return true;
                        }
                    } else {
                        count = 0;
                    }
                    if (field[j][fieldSizeX - 1 - i - j] == dot) {
                        reverseCount++;
                        if (reverseCount == WIN_COUNT) {
                            return true;
                        }
                    } else {
                        reverseCount = 0;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Проверка на ничью
     * @return
     */
    static boolean checkDraw(){
        for (int i = 0; i < fieldSizeY; i++){
            for (int j = 0; j < fieldSizeX; j++){
                if (isCellEmpty(i, j)) return false;
            }
        }
        return true;
    }

}
