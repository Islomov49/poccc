package com.jim.pocketaccounter.syncbase;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jim.pocketaccounter.PocketAccounter;
import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.ReckingCredit;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by DEV on 16.06.2016.
 */

public class SignInGoogleMoneyHold {
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    String TAG="MainAct";
    Context context;
    UpdateSucsess succsesEvent;
    SharedPreferences spref;
    SharedPreferences.Editor ed  ;

    public interface UpdateSucsess{
        public void updateToSucsess();
        public void updateToFailed();

    }
    public SignInGoogleMoneyHold(Context con,UpdateSucsess succsesEventik){
        context=con;
        spref=con.getSharedPreferences("infoFirst",con.MODE_PRIVATE);
        ed=spref.edit();

        this.succsesEvent=succsesEventik;

        GoogleApiClient.OnConnectionFailedListener conFaild=new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                succsesEvent.updateToFailed();
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((PocketAccounter)context /* FragmentActivity */, conFaild /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuth=FirebaseAuth.getInstance();
    }
    public static final String DATA_BASE="PocketAccounterDatabase.db";
    public static final int RC_SIGN_IN=10011;

    public void regitUser(){
        if(!SyncBase.isNetworkAvailable(context)){
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage(R.string.connection_faild)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });
            builder.create().show();
            return;
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((PocketAccounter)context).startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    public void regitRequstGet(Intent data){



        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        Log.d(TAG,data.toString());

        if (result.isSuccess()) {
            showProgressDialog();
            GoogleSignInAccount account = result.getSignInAccount();
            Log.d(TAG,account.getDisplayName());
            if(account.getPhotoUrl()!=null)
            Log.d(TAG,account.getPhotoUrl().toString());
            Log.d(TAG,account.getEmail());
            firebaseAuthWithGoogle(account);
            ed.putBoolean("FIRSTSYNC",false);
            ed.commit();
        } else {
            hideProgressDialog();
            succsesEvent.updateToFailed();

        }
    }
    public void revokeAccess() {

         mAuth.signOut();

            mGoogleApiClient.connect();
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    FirebaseAuth.getInstance().signOut();
                    if(mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                 if (status.isSuccess()) {
                                        }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            });
    }



    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((PocketAccounter)context ), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            succsesEvent.updateToFailed();
                        }
                        else {
                            succsesEvent.updateToSucsess();
                        }
                        hideProgressDialog();
                    }
                });
    }



    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    public void openDialog() {
        final Dialog dialog = new Dialog(context);
        final View dialogView = ((PocketAccounter) context).getLayoutInflater().inflate(R.layout.first_login_info, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        final SignInButton singinbut = (SignInButton) dialogView.findViewById(R.id.signg);

        ImageView cancel = (ImageView) dialogView.findViewById(R.id.ivInfoDebtBorrowCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        singinbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regitUser();
                dialog.dismiss();

            }
        });
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(7 * width / 8, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }



}
