package server;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


public class car {

    private String photo="";
    private String modele="";

    public car() {
    }

    public car(String modele, String photo) {
        this.modele=modele;
        if(photo==null)
            this.photo=findPhotoForModele(modele);
        else
            this.photo=photo;
    }

    private String findPhotoForModele(String modele) {
        Tools.toJSON("./assets/modeles.json");


        if(modele.toLowerCase().contains("scenic"))return "https://www.cdn.renault.com/content/dam/Renault/master/vehicules/scenic-jfa-ph1/reveal/renault-scenic-jfa-ph1-preview-video-001.jpg.ximg.s_12_h.smart.jpg";
        if(modele.toLowerCase().contains("série 5"))return "https://www.turbo.fr/sites/default/files/styles/article_690x405/public/2018-07/logo_690x405_turbo_bmw_serie_5.png?itok=Muo0xXLV";
        if(modele.toLowerCase().contains("série 1"))return "https://cdn.carizy.com/carphotos/4017/wide/bmw-serie-1-occasion-2011-avant-gauche.jpg";
        if(modele.toLowerCase().contains("x5"))return "https://www.lobservateur-automobiles.com/wp-content/uploads/2018/06/2-3-e1528297188324.jpg";
        if(modele.toLowerCase().contains("clio"))return "https://images.elite-auto.fr/visuel/RENAULT/renault_17cliointens5ha4fb_angularfront.png";
        if(modele.toLowerCase().contains("kangoo"))return "";
        if(modele.toLowerCase().contains("scénic"))return "";

        if(modele.toLowerCase().contains("308"))return "https://img.autoplus.fr/picture/peugeot/308/1465820/peugeot_308_2013_0d075-1600-1108.jpg?r";
        if(modele.toLowerCase().contains("508"))return "https://www.largus.fr/images/images/0mm00nf4-1pr8a5hzzzzzzzb0-zzzzzzzz-001-01-redimensionner.png?width=612&quality=80";
        if(modele.toLowerCase().contains("208"))return "https://www.ouestfrance-auto.com/p/yahooto/11800867_1_134816_06d366372da9fe4e62564485c0994604_crop_740-555_.jpg";

        return "https://www.challenges.fr/assets/img/2018/08/27/cover-r4x3w1000-5b84072224873-pbc18-conference-09-jpg.jpg";
    }

    public String getModele() {
        return modele;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

}
