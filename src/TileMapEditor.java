import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TileMapEditor extends JFrame {
    private int[][] tileMap;
    private int currentTile;

    SetTileButtonListener setTileButtonListener;

    private static final String[] TILE_IMAGE_PATHS = {
            "/tiles/grass.png",
            "/tiles/brickwall.png",
            "/tiles/water.png",
            "/tiles/earth.png",
            "/tiles/sand.png",
            "/tiles/tree.png"
            // Add more paths as needed
    };


    private ImageIcon[] tileIcons;

    public TileMapEditor() {
        super("Tile Map Editor");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tileMap = new int[50][50];
        currentTile = 0;

        setTileButtonListener = new SetTileButtonListener(0);

        loadTileIcons();
        createGUI();
    }

    private void loadTileIcons() {
        tileIcons = new ImageIcon[TILE_IMAGE_PATHS.length];

        for (int i = 0; i < TILE_IMAGE_PATHS.length; i++) {
            tileIcons[i] = new ImageIcon(getClass().getResource(TILE_IMAGE_PATHS[i]));
            if (tileIcons[i].getImage() == null) {
                System.out.println("Error loading image: " + TILE_IMAGE_PATHS[i]);
            }
        }
    }


    private ImageIcon resizeImage(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open a dialog for the user to input the file name
            String fileName = JOptionPane.showInputDialog(TileMapEditor.this, "Enter file name:");
            if (fileName != null && !fileName.trim().isEmpty()) {
                saveToFile(fileName);
            }
        }
    }

    private void saveToFile(String fileName) {
        String directoryPath = "res/maps";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = directoryPath + File.separator + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".txt"))) {
            for (int row = 0; row < 50; row++) {
                for (int col = 0; col < 50; col++) {
                    writer.write(Integer.toString(tileMap[row][col]));
                    writer.write(" ");
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(TileMapEditor.this, "Map saved successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(TileMapEditor.this, "Error saving map to file!");
        }
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        JPanel mapPanel = new JPanel(new GridLayout(50, 50));
        JButton[][] tileButtons = new JButton[50][50];

        for (int row = 0; row < 50; row++) {
            for (int col = 0; col < 50; col++) {
                tileButtons[row][col] = new JButton();
                tileButtons[row][col].setIcon(tileIcons[tileMap[row][col]]);
                tileButtons[row][col].addActionListener(new TileButtonListener(row, col));
                mapPanel.add(tileButtons[row][col]);
            }
        }

        JPanel buttonPanel = new JPanel();
        for (int i = 0; i < TILE_IMAGE_PATHS.length; i++) {
            String fileName = new File(TILE_IMAGE_PATHS[i]).getName();
            // Remove the ".png" extension
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            // Capitalize the first letter
            fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1).toLowerCase();
            JButton tileButton = new JButton(fileName);
            tileButton.addActionListener(new SetTileButtonListener(i));
            buttonPanel.add(tileButton);
        }

        JButton saveButton = new JButton("Save Map");
        saveButton.addActionListener(new SaveButtonListener());
        buttonPanel.add(saveButton);

        add(mapPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private class TileButtonListener implements ActionListener {
        private int row;
        private int col;

        public TileButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tileMap[row][col] = currentTile;
            ImageIcon resizedIcon = resizeImage(tileIcons[currentTile], 16, 16);
            ((JButton) e.getSource()).setIcon(resizedIcon);
            System.out.println("Tile at row " + row + " and column " + col + " is of type: " + tileMap[row][row]);
        }
    }

    private class SetTileButtonListener implements ActionListener {
        private int tileIndex;

        public SetTileButtonListener(int tileIndex) {
            this.tileIndex = tileIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentTile = tileIndex;

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TileMapEditor editor = new TileMapEditor();
            editor.setVisible(true);
        });
    }
}
