package com.jim.pocketaccounter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jim.pocketaccounter.credit.AdapterCridet;
import com.jim.pocketaccounter.credit.AdapterCridetArchive;
import com.jim.pocketaccounter.credit.CreditDetials;
import com.jim.pocketaccounter.credit.LinearManagerWithOutEx;
import com.jim.pocketaccounter.finance.FinanceManager;

import java.util.ArrayList;



public class CreditFragment extends Fragment {
    ArrayList<CreditDetials> crList;
    ArrayList<CreditDetials> creditDetialsesList;
    RecyclerView crRV;
    AdapterCridet crAdap;
    Context This;
    CreditTabLay.SvyazkaFragmentov svyaz;
    private FinanceManager financeManager;
    private CreditTabLay creditTabLay;

    public AdapterCridetArchive.GoCredFragForNotify getInterfaceNotify(){
        return new AdapterCridetArchive.GoCredFragForNotify() {
            @Override
            public void notifyCredFrag() {
                crAdap.notifyDataSetChanged();

            }
        };
    }

    public CreditFragment() {
        // Required empty public constructor

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crList=PocketAccounter.financeManager.getCredits();
        This=getActivity();



    }
    public  CreditTabLay.ForFab getEvent(){
        return new CreditTabLay.ForFab() {
            @Override
            public void pressedFab() {
                openFragment(new AddCreditFragment(),AddCreditFragment.OPENED_TAG);
            }
        };
    }
    public void setSvyaz(CreditTabLay.SvyazkaFragmentov A){
        svyaz=A;
    }
    Toolbar toolbarik;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        toolbarik=PocketAccounter.toolbar;
        ImageView ivToolbarMostRight = (ImageView) toolbarik.findViewById(R.id.ivToolbarMostRight);
        ivToolbarMostRight.setImageResource(R.drawable.ic_delete_black);
        ivToolbarMostRight.setVisibility(View.GONE);
        toolbarik.setTitle(R.string.cred_managment);
        toolbarik.setSubtitle("");
        toolbarik.findViewById(R.id.spToolbar).setVisibility(View.GONE);

        View V=inflater.inflate(R.layout.fragment_credit, container, false);
        financeManager = PocketAccounter.financeManager;
        crRV=(RecyclerView) V.findViewById(R.id.my_recycler_view);
        LinearManagerWithOutEx llm = new LinearManagerWithOutEx(This);
        crRV.setLayoutManager(llm);

        crAdap=new AdapterCridet(This,svyaz);
        crRV.setAdapter(crAdap);

        crRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                creditTabLay.onScrolledList(dy > 0);
            }
        });
        return V;
    }

    public void setCreditTabLay (CreditTabLay creditTabLay) {
        this.creditTabLay = creditTabLay;
    }

    public void openFragment(Fragment fragment,String tag) {
        if (fragment != null) {
            if(tag.matches("Addcredit"))
                ((AddCreditFragment)fragment).addEventLis(new EventFromAdding() {
                    @Override
                    public void addedCredit() {
                        updateToFirst();
                    }

                    @Override
                    public void canceledAdding() {
                    }
                });
            final android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(tag).setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.flMain, fragment,tag);
            ft.commit();
        }
    }
    public void updateToFirst(){

        try{
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    crAdap.notifyItemInserted(0);
                }
            }, 50);

            try {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crRV.scrollToPosition(0);
                    }
                }, 100);
            }
            catch (Exception o){
            }

        }
        catch (Exception o){

        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onStop() {
        super.onStop();
        //  PocketAccounter.financeManager.setCredits(crList);
        PocketAccounter.financeManager.saveCredits();

    }
    @Override
    public void onDetach() {
        super.onDetach();
        PocketAccounter.financeManager.saveCredits();
    }
    public interface EventFromAdding{
        void addedCredit();
        void canceledAdding();
    }


}
