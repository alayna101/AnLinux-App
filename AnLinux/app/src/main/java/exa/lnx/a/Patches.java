package exa.lnx.a;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;
import java.util.Date;

public class Patches extends Fragment {

    Context context;
    Button button;
    Button button2;
    Button button3;
    TextView textView;
    TextView textView2;
    TextView textView3;
    String patches;
    String s;
    boolean shouldShowAds;
    SharedPreferences sharedPreferences;
    InterstitialAd mInterstitialAd;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        getActivity().setTitle(R.string.patches);

        View view = inflater.inflate(R.layout.patches, container, false);

        context = getActivity().getApplicationContext();
        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-5748356089815497/1086414838");

        if(!donationInstalled() && !isVideoAdsWatched()){
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            shouldShowAds = true;
        }

        patches = "Nothing";
        s = Build.SUPPORTED_ABIS[0];

        button = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);

        textView = view.findViewById(R.id.textView);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyUserToChoosePatches();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                if(patches.equals("Ashmem")){
                    if(s.equals("arm64-v8a")){
                        ClipData clip = ClipData.newPlainText("Command", "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/aarch64/install-ashmem.sh && bash install-ashmem.sh");
                        clipboard.setPrimaryClip(clip);
                    }else if(s.contains("arm")){
                        ClipData clip = ClipData.newPlainText("Command", "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/armhf/install-ashmem.sh && bash install-ashmem.sh");
                        clipboard.setPrimaryClip(clip);
                    }else if(s.equals("x86")){
                        ClipData clip = ClipData.newPlainText("Command", "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/i386/install-ashmem.sh && bash install-ashmem.sh");
                        clipboard.setPrimaryClip(clip);
                    }else if(s.equals("x86_64")){
                        ClipData clip = ClipData.newPlainText("Command", "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/amd64/install-ashmem.sh && bash install-ashmem.sh");
                        clipboard.setPrimaryClip(clip);
                    }
                }else if(patches.equals("SECCOMP")){
                    ClipData clip = ClipData.newPlainText("Command", "echo \"export PROOT_NO_SECCOMP=1\" >> .bashrc && hash -r");
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(context, getString(R.string.command_copied), Toast.LENGTH_SHORT).show();
                if(mInterstitialAd != null && mInterstitialAd.isLoaded() && shouldShowAds){
                    if(!donationInstalled() && !isVideoAdsWatched()){
                        mInterstitialAd.show();
                    }
                    shouldShowAds = false;
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.termux");
                if(isPackageInstalled("com.termux", context.getPackageManager())){
                    startActivity(intent);
                }else{
                    notifyUserForInstallTerminal();
                }
            }
        });
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        return view;
    }
    public void notifyUserToChoosePatches(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.patches_chooser, nullParent);
        final CheckBox checkBox = view.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = view.findViewById(R.id.checkBox2);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        if(patches.equals("Ashmem")){
            checkBox.setChecked(true);
        }else if(patches.equals("SECCOMP")){
            checkBox2.setChecked(true);
        }
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(checkBox.isChecked()){
                    if(!patches.equals("Ashmem")){
                        shouldShowAds = true;
                        patches = "Ashmem";
                    }
                }else if(checkBox2.isChecked()){
                    if(!patches.equals("SECCOMP")){
                        shouldShowAds = true;
                        patches = "SECCOMP";
                    }
                }
                if(patches.equals("Ashmem")){
                    if(s.equals("arm64-v8a")){
                        textView.setText(R.string.ashmem_step1);
                        textView2.setText(getString(R.string.ashmem_step2, "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/aarch64/install-ashmem.sh && bash install-ashmem.sh", "ashmem"));
                        textView3.setText(R.string.ashmem_step3);
                    }else if(s.contains("arm")){
                        textView.setText(R.string.ashmem_step1);
                        textView2.setText(getString(R.string.ashmem_step2, "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/armhf/install-ashmem.sh && bash install-ashmem.sh", "ashmem"));
                        textView3.setText(R.string.ashmem_step3);
                    }else if(s.equals("x86")){
                        textView.setText(R.string.ashmem_step1);
                        textView2.setText(getString(R.string.ashmem_step2, "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/i386/install-ashmem.sh && bash install-ashmem.sh", "ashmem"));
                        textView3.setText(R.string.ashmem_step3);
                    }else if(s.equals("x86_64")){
                        textView.setText(R.string.ashmem_step1);
                        textView2.setText(getString(R.string.ashmem_step2, "wget https://raw.githubusercontent.com/EXALAB/AnLinux-Resources/master/Library/Ashmem/amd64/install-ashmem.sh && bash install-ashmem.sh", "ashmem"));
                        textView3.setText(R.string.ashmem_step3);
                    }
                }else if(patches.equals("SECCOMP")){
                    textView.setText(R.string.seccomp_step1);
                    textView2.setText(R.string.seccomp_step2);
                    textView3.setText(R.string.seccomp_step3);
                }
                button2.setEnabled(true);
                button3.setEnabled(true);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void notifyUserForInstallTerminal(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://details?id=com.termux");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(Build.VERSION.SDK_INT >= 21){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try{
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.termux")));
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText(R.string.termux_not_Installed);
    }
    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean donationInstalled() {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkSignatures(context.getPackageName(), "exa.lnx.d") == PackageManager.SIGNATURE_MATCH;
    }
    private boolean isVideoAdsWatched(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        cal.setTime(date);
        int a =  cal.get(Calendar.DAY_OF_MONTH);
        int b = sharedPreferences.getInt("VideoAds", 0);
        return a == b;
    }
}
