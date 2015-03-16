package pl.szjug.swing

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics}
import javax.swing.{JComponent, JFrame, WindowConstants}

case class ImgFrame(img: BufferedImage) {

  private val f = new JFrame()
  private val panel = new ImgPanel(img)

  f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  f.getContentPane.add(panel)
  f.setSize(800, 600)
  f.setVisible(true)

  def repaint(): Unit = {
    panel.repaint()
  }

  def repaintImagePart(x: Int, y: Int, width: Int, height: Int): Unit = {
    val px = x * panel.getWidth / img.getWidth
    val py = y * panel.getHeight / img.getHeight
    val pWidth = width * panel.getWidth / img.getWidth + 5
    val pHeight = height * panel.getHeight / img.getHeight + 5

    panel.repaint(px, py, pWidth, pHeight)
  }
}

class ImgPanel(img: BufferedImage) extends JComponent {

  override protected def paintComponent(g: Graphics): Unit = {
    g.drawImage(img, 0, 0, getWidth, getHeight, 0, 0, img.getWidth, img.getHeight, Color.BLACK, null)
  }
}
