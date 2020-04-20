package com.kingominho.monchridiario.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kingominho.monchridiario.adapters.AboutAdapter;
import com.kingominho.monchridiario.models.AboutItem;
import com.kingominho.monchridiario.models.AboutTitleItem;

import java.util.ArrayList;
import java.util.List;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private AboutAdapter aboutAdapter;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        int[] surrogates = {0x2764, 0xfe0f};
        String heartEmojiString = new String(surrogates, 0, surrogates.length);
        String s = "Made with " + heartEmojiString + " by KingoMinho.";
        mText.setValue(s);
        aboutAdapter = new AboutAdapter(initData());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public AboutAdapter getAboutAdapter() {
        return aboutAdapter;
    }

    private List<Object> initData(){
        List<Object> list = new ArrayList<>();

        list.add(new AboutTitleItem("Project repository"));
        list.add(new AboutItem("Mon Chéri Diario", "By KingoMinho",
                "https://github.com/the-it-weirdo/Mon_cheri_Diario"));

        list.add(new AboutTitleItem("Third Party Libraries used: "));
        list.add(new AboutItem("Android Image Cropper", "For Image cropping.",
                "https://github.com/ArthurHub/Android-Image-Cropper"));
        list.add(new AboutItem("Picasso Transformations", "For circular ImageView." ,
                "https://github.com/wasabeef/picasso-transformations"));
        list.add(new AboutItem("Picasso", "For handling images.",
                "https://square.github.io/picasso/"));

        list.add(new AboutTitleItem("Resources used: "));
        list.add(new AboutItem("App Icon", "from PNG Repo",
                "https://www.pngrepo.com/svg/246093/diary"));
        list.add(new AboutItem("Paper texture", "from Freepik",
                "http://www.freepik.com"));
        list.add(new AboutItem("Emoji hex codes", "from EMOJIGUIDE.ORG",
                "https://emojiguide.org/"));


        list.add(new AboutTitleItem("Special Thanks: "));
        list.add(new AboutItem("Coding in Flow", "A special thanks to Florian Walther for creating " +
                "awesome tutorials on Android Development.", "https://www.youtube.com/channel/UC_Fh8kvtkVPkeihBs42jGcA"));
        list.add(new AboutItem("Stack Overflow", "For solving countless doubts.",
                "https://stackoverflow.com/"));
        list.add(new AboutItem("Very special thanks to my friends Acquib, Devashish, Rajani and Shouvit",
                "for providing valuable feedback and suggestions and helping me test my app and bearing with my constant nagging.",
                ""));

        return list;
    }
}