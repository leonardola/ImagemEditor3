package imagemeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Cendron Date 26/02/2016
 */
public class Janela extends JFrame {

    private JDesktopPane janelaPrincipal;

    private final JProgressBar barraProgresso = new JProgressBar();
    private final JButton botaoCancela = new JButton("Cancelar");
    private final JLabel campoTexto = new JLabel();
    private final ImageIcon imageIcon = new ImageIcon();
    private final JLabel campoImagem = new JLabel();
    private BufferedImage m_imagem, originalImage;

    private trataImagem editorImagem;

    public Janela() {
        super("Editor de imagem");
        setLayout(new BorderLayout());
        editorImagem = new trataImagem();
        // Menu
        JMenuBar barraOpcoes = new JMenuBar();
        JMenu arquivoMenu = new JMenu("Arquivo");
        JMenuItem abrirArquivo = new JMenuItem("Abrir imagem");

        abrirArquivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "JPG, PNG & GIF Images", "jpg", "gif", "jpeg", "png");
                
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        
                        campoTexto.setText("Abrindo: " + file.getName());
                        if (file == null) {
                            return;
                        }
                        originalImage = ImageIO.read(file);
                        alteraImagem(originalImage);

                    } catch (IOException ex) {
                        Logger.getLogger(Janela.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                     campoTexto.setText("Usuário cancelou");
                }
            }
        });
        
        JMenuItem salvaArquivo = new JMenuItem("Salva imagem");
        
        salvaArquivo.addActionListener((ActionEvent e) -> {
            if (this.m_imagem == null) {
                JOptionPane.showMessageDialog(null, "Execute algum processamento na imagem", "Erro salvando", JOptionPane.ERROR_MESSAGE);
                

            } else {
                campoTexto.setText("Salvando imagem");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvar imagem...");
                int opcao = fileChooser.showSaveDialog(null);
                if (opcao == JFileChooser.APPROVE_OPTION) {
                    File arquivo = fileChooser.getSelectedFile();
                    editorImagem.saveImage(this.m_imagem, arquivo);
                    JOptionPane.showMessageDialog(null, "Arquivo foi salvo", "Arquivo salvo", JOptionPane.INFORMATION_MESSAGE);
                }
                campoTexto.setText("Imagem salva");
            }

        });

        arquivoMenu.add(abrirArquivo);
        arquivoMenu.add(salvaArquivo);

        barraOpcoes.add(arquivoMenu);
        
        JMenu imagemMenu = new JMenu("Editar Imagem");
        
        JMenuItem aplicarImagemOriginal = new JMenuItem("Imagem original");
        aplicarImagemOriginal.addActionListener((ActionEvent e) -> {
            alteraImagem(this.originalImage);
            this.m_imagem = null;
            campoTexto.setText("Imagem original");
        });
        imagemMenu.add(aplicarImagemOriginal);
        
        JMenuItem aplicarCinza = new JMenuItem("Converter para Escala de Cinza");
        aplicarCinza.addActionListener((ActionEvent e) -> {
            this.m_imagem = editorImagem.imageToBW(this.originalImage, this.barraProgresso, this.campoTexto);
            alteraImagem(this.m_imagem);
        });
        imagemMenu.add(aplicarCinza);
        
        JMenuItem aplicarSepia = new JMenuItem("Aplicar Sépia");
        aplicarSepia.addActionListener((ActionEvent e) -> {
            this.m_imagem = editorImagem.applySepia(this.originalImage, this.barraProgresso, this.campoTexto);
            alteraImagem(this.m_imagem);
        });
        
        JMenuItem aplicarConvolucao = new JMenuItem("Aplicar Convolução");
        aplicarConvolucao.addActionListener((ActionEvent e) -> {
             /*
            Alguns kernels podem ser encontrados em: http://aishack.in/tutorials/image-convolution-examples/
            Outros ainda podem ser encontrados na internet.        
            */
            double[][] kernel = { // Operador de borda de Sobel
                {-1, -2, -1},
                {1, -4, 1},
                {1, 2, 1}
            };
            
           this.m_imagem = editorImagem.convolution2D(this.originalImage, kernel, this.barraProgresso, this.campoTexto);
            alteraImagem(this.m_imagem);
        });
        
        JMenuItem menuFFT = new JMenuItem("Realiza Transformação de Fourier");
        menuFFT.addActionListener((ActionEvent e) -> {
          campoTexto.setText("FFT inicializado"); 
            
          this.m_imagem = editorImagem.geraFFT(this.originalImage, this.barraProgresso, this.campoTexto);
          alteraImagem(this.m_imagem);
        });
        
        // Novas funcionalidades: 
        
        JMenuItem menuHistograma = new JMenuItem("Cria Histograma");
        menuHistograma.addActionListener((ActionEvent e) -> {
          campoTexto.setText("Cria histograma"); 
            
          this.m_imagem = Histogram.criaHistograma(this.originalImage, this.barraProgresso, this.campoTexto);
          alteraImagem(this.m_imagem);
        });
        
        JMenuItem menuThreshold = new JMenuItem("Aplica Threshold");
        menuThreshold.addActionListener((ActionEvent e) -> {
            campoTexto.setText("Aplica Threshold");
            this.m_imagem = editorImagem.thresholdImg(this.originalImage, this.barraProgresso, this.campoTexto);
            alteraImagem(this.m_imagem);
        });
        
        JMenuItem menuEqualiza = new JMenuItem("Equaliza Imagem");
        menuEqualiza.addActionListener((ActionEvent e) -> {
            campoTexto.setText("Equaliza Imagem");
            this.m_imagem = editorImagem.equalizaImg(this.originalImage, this.barraProgresso, this.campoTexto);
            alteraImagem(this.m_imagem);
        });
        
        JMenuItem menuRedimensiona = new JMenuItem("Redimensiona Imagem");
        menuRedimensiona.addActionListener((ActionEvent e) -> {
            campoTexto.setText("Redimensiona Imagem");
            String inputValue = JOptionPane.showInputDialog("Digite o fator de Ampliação/redução");
            double fator = Double.parseDouble(inputValue);
            if (fator > 0) {
                this.m_imagem = editorImagem.resizeBilinear(this.originalImage, 
                        (int) (this.originalImage.getWidth() * fator), 
                        (int) (this.originalImage.getHeight() * fator),
                        this.barraProgresso, this.campoTexto);

                alteraImagem(this.m_imagem);
            }
        });

        
        
        imagemMenu.add(aplicarImagemOriginal);
        imagemMenu.add(aplicarCinza);
        imagemMenu.add(aplicarSepia);
        imagemMenu.add(aplicarConvolucao);
        imagemMenu.add(menuFFT);
        imagemMenu.addSeparator();
        imagemMenu.add(menuHistograma);
        imagemMenu.add(menuThreshold);
        imagemMenu.add(menuEqualiza);
        imagemMenu.add(menuRedimensiona);
        
        
        barraOpcoes.add(imagemMenu);       
        setJMenuBar(barraOpcoes);
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        botaoCancela.setEnabled(true);
        botaoCancela.addActionListener(
                (ActionEvent e) -> {
                    System.out.println("Cancelado");
                }
        );

        infoPanel.add(botaoCancela);

        barraProgresso.setValue(0);
        barraProgresso.setStringPainted(true);
        infoPanel.add(barraProgresso);

        campoTexto.setText("Aguardando...");
        infoPanel.add(campoTexto);

        add(infoPanel, BorderLayout.SOUTH);
        add(campoImagem, BorderLayout.CENTER);
        
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
                if (m_imagem != null){
                    alteraImagem(m_imagem);
                }
                else if(originalImage != null){
                    alteraImagem(originalImage);
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 480);
        setVisible(true);

    }

    private void alteraImagem(BufferedImage m_imagem) {

        campoImagem.setIcon(null);
        Dimension sizeCampo = campoImagem.getSize();

        imageIcon.setImage(trataImagem.rescale(m_imagem, sizeCampo.width, sizeCampo.height));
        campoImagem.setIcon(imageIcon);

    }
    
    public void alteraTexto(String texto){
         this.campoTexto.setText(texto);
    }

}
