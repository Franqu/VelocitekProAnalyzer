package velocitekProStartAnalyzer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



  public  class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                MainWindow.popup.show(e.getComponent(),
                           e.getX(), e.getY());
           
        }
    }
}