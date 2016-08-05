package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionBarOverlayLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.finance.IconAdapterAccount;
import com.jim.pocketaccounter.finance.IconAdapterCategory;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;
import com.jim.pocketaccounter.finance.SubCategoryAdapter;
import com.jim.pocketaccounter.helper.FABIcon;
import com.jim.pocketaccounter.helper.PockerTag;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

@SuppressLint({"InflateParams", "ValidFragment"})
public class RootCategoryEditFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private EditText etCatEditName;
	private CheckBox chbCatEditExpanse, chbCatEditIncome;
	private FABIcon fabCatIcon;
	private ImageView ivSubCatAdd, ivSubCatDelete, ivToolbarMostRight;
	private ListView lvSubCats;
	private RootCategory category;
	private int mode = PocketAccounterGeneral.NORMAL_MODE, type;
	private String selectedIcon = "";
	private boolean[] selected;
	private String[] icons;
	private ArrayList<SubCategory> subCategories;
	private int edit_mode, pos;
	private String subcatIcon;
	private Calendar calendar;
	private String categoryId;
	private boolean isChanged;
	public RootCategoryEditFragment(RootCategory category, int mode, int pos, Calendar date) {
		this.category = category;
		if (category == null)
			categoryId = "category_"+UUID.randomUUID().toString();
		else
			categoryId = category.getId();
		this.edit_mode = mode;
		this.pos = pos;
		if (date != null)
			calendar = (Calendar) date.clone();
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cat_edit_layout, container, false);
		((PocketAccounter)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_button);
		((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);

		PocketAccounter.toolbar.setTitle(R.string.category);
		PocketAccounter.toolbar.setSubtitle(R.string.edit);
		PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				v.postDelayed(new Runnable() {
					@Override
					public void run() {

						if (edit_mode == PocketAccounterGeneral.NO_MODE)
							((PocketAccounter)getContext()).replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
						else {
							((PocketAccounter)getContext()).initialize(calendar);
							((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
						}
					}
				},50);
			}
		});
		ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
		ivToolbarMostRight.setImageDrawable(null);
		ivToolbarMostRight.setImageResource(R.drawable.check_sign);
		ivToolbarMostRight.setOnClickListener(this);
		etCatEditName = (EditText) rootView.findViewById(R.id.etAccountEditName);
		chbCatEditExpanse = (CheckBox) rootView.findViewById(R.id.chbCatEditExpanse);
		chbCatEditIncome = (CheckBox) rootView.findViewById(R.id.chbCatEditIncome);
		if (edit_mode == PocketAccounterGeneral.EXPANSE_MODE) {
			chbCatEditExpanse.setChecked(true);
			chbCatEditIncome.setChecked(false);

		}
		if (edit_mode == PocketAccounterGeneral.INCOME_MODE) {
			chbCatEditExpanse.setChecked(false);
			chbCatEditIncome.setChecked(true);

		}
		chbCatEditExpanse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (edit_mode == PocketAccounterGeneral.NO_MODE) {
					chbCatEditExpanse.setChecked(isChecked);
					chbCatEditIncome.setChecked(!isChecked);
				}
				if (edit_mode == PocketAccounterGeneral.EXPANSE_MODE) {
					chbCatEditExpanse.setChecked(true);
					chbCatEditIncome.setChecked(false);
				}
				if (edit_mode == PocketAccounterGeneral.INCOME_MODE) {
					chbCatEditExpanse.setChecked(false);
					chbCatEditIncome.setChecked(true);
				}
				isChanged = (category != null && category.getType() == PocketAccounterGeneral.INCOME);
			}
		});
		chbCatEditIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (edit_mode == PocketAccounterGeneral.NO_MODE) {
					chbCatEditExpanse.setChecked(!isChecked);
					chbCatEditIncome.setChecked(isChecked);
				}
				if (edit_mode == PocketAccounterGeneral.EXPANSE_MODE) {
					chbCatEditExpanse.setChecked(true);
					chbCatEditIncome.setChecked(false);
				}
				if (edit_mode == PocketAccounterGeneral.INCOME_MODE) {
					chbCatEditExpanse.setChecked(false);
					chbCatEditIncome.setChecked(true);
				}
				isChanged = (category != null && category.getType() == PocketAccounterGeneral.EXPENSE);
			}
		});
		fabCatIcon = (FABIcon) rootView.findViewById(R.id.fabAccountIcon);
		fabCatIcon.setOnClickListener(this);
		ivSubCatAdd = (ImageView) rootView.findViewById(R.id.ivSubCatAdd);
		ivSubCatAdd.setOnClickListener(this);
		ivSubCatDelete = (ImageView) rootView.findViewById(R.id.ivSubCatDelete);
		ivSubCatDelete.setOnClickListener(this);
		lvSubCats = (ListView) rootView.findViewById(R.id.lvAccountHistory);
		lvSubCats.setOnItemClickListener(this);
		icons = getResources().getStringArray(R.array.icons);

		type = PocketAccounterGeneral.EXPENSE;
		selectedIcon = icons[0];
		subCategories = new ArrayList<SubCategory>();
		mode = PocketAccounterGeneral.NORMAL_MODE;
		setMode(mode);
		if (category != null) {
			etCatEditName.setText(category.getName());
			chbCatEditIncome.setChecked(false);
			chbCatEditExpanse.setChecked(false);
			switch(category.getType()) {
			case PocketAccounterGeneral.INCOME:
				chbCatEditIncome.setChecked(true);
				break;
			case PocketAccounterGeneral.EXPENSE:
				chbCatEditExpanse.setChecked(true);
				break;
			}
			type = category.getType();
			selectedIcon = category.getIcon();
			subCategories = category.getSubCategories();
			refreshSubCatList(mode);
		}
		int resId = getResources().getIdentifier(selectedIcon, "drawable", getContext().getPackageName());
		Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap icon = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp), (int)getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabCatIcon.setImageBitmap(icon);
		return rootView;
	}
	private void refreshSubCatList(int mode) {
		SubCategoryAdapter adapter = new SubCategoryAdapter(getActivity(), subCategories, selected, mode);
		lvSubCats.setAdapter(adapter);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE)
			openSubCatEditDialog(subCategories.get(position));
		else {
			CheckBox chbSubCat = (CheckBox)view.findViewById(R.id.chbSubCat);
			chbSubCat.setChecked(!chbSubCat.isChecked());
			selected[position] = chbSubCat.isChecked();
		}
	}
	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		switch(v.getId()) {
		case R.id.fabAccountIcon:
			openIconsDialog();
			break;
		case R.id.ivSubCatAdd:
			openSubCatEditDialog(null);
			break;
		case R.id.ivSubCatDelete:
			if (mode == PocketAccounterGeneral.NORMAL_MODE) {
				mode = PocketAccounterGeneral.EDIT_MODE;
				setMode(mode);
			}
			else {
				mode = PocketAccounterGeneral.NORMAL_MODE;
				boolean isAnySelected = false;
				for (int i=0; i<selected.length; i++) {
					if (selected[i]) {
						isAnySelected = true;
						break;
					}
				}
				if (isAnySelected) {
					final Dialog dialog=new Dialog(getContext());
					View dialogView = getActivity().getLayoutInflater().inflate(R.layout.warning_dialog, null);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(dialogView);
					TextView tv = (TextView) dialogView.findViewById(R.id.tvWarningText);
					tv.setText(R.string.subcat_delete_warning);
					Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
					btnYes.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteSubcats();
							setMode(mode);
							dialog.dismiss();
						}
					});
					Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
					btnNo.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
				else
					setMode(mode);
			}
			break;
		case R.id.ivToolbarMostRight:
			if (etCatEditName.getText().toString().matches("")) {
				etCatEditName.setError(getResources().getString(R.string.category_name_error));
				return;
			}
			if (!chbCatEditIncome.isChecked() && !chbCatEditExpanse.isChecked()) {
				Toast.makeText(getActivity(), getResources().getString(R.string.cat_type_not_choosen), Toast.LENGTH_SHORT).show();
				Animation wobble = AnimationUtils.loadAnimation(getActivity(), R.anim.wobble);
				((LinearLayout)chbCatEditIncome.getParent()).startAnimation(wobble);
				return;
			}
			if (chbCatEditIncome.isChecked())
				type = PocketAccounterGeneral.INCOME;
			if (chbCatEditExpanse.isChecked())
				type = PocketAccounterGeneral.EXPENSE;
			if (edit_mode == PocketAccounterGeneral.NO_MODE) {
				if (category != null) {
					category.setName(etCatEditName.getText().toString());
					if (isChanged) {
						if (category.getType() == PocketAccounterGeneral.EXPENSE) {
							for (int i=0; i<PocketAccounter.financeManager.getExpanses().size(); i++) {
								if (PocketAccounter.financeManager.getExpanses().get(i) == null) continue;
								if (PocketAccounter.financeManager.getExpanses().get(i).getId().matches(category.getId())) {
									PocketAccounter.financeManager.getExpanses().set(i, null);
								}
							}
						} else {
							for (int i=0; i<PocketAccounter.financeManager.getIncomes().size(); i++) {
								if (PocketAccounter.financeManager.getIncomes().get(i) == null) continue;
								if (PocketAccounter.financeManager.getIncomes().get(i).getId().matches(category.getId())) {
									PocketAccounter.financeManager.getIncomes().set(i, null);
								}
							}
						}
					}
					category.setType(type);
					category.setIcon(selectedIcon);
					category.setSubCategories(subCategories);
					for (int i=0; i<PocketAccounter.financeManager.getExpanses().size(); i++) {
						if (PocketAccounter.financeManager.getExpanses().get(i) == null) continue;
						if (PocketAccounter.financeManager.getExpanses().get(i).getId().matches(category.getId())) {
							PocketAccounter.financeManager.getExpanses().set(i, category);
						}
					}
					for (int i=0; i<PocketAccounter.financeManager.getIncomes().size(); i++) {
						if (PocketAccounter.financeManager.getIncomes().get(i) == null) continue;
						if (PocketAccounter.financeManager.getIncomes().get(i).getId().matches(category.getId())) {
							PocketAccounter.financeManager.getIncomes().set(i, category);
						}
					}
				}
				else {
					RootCategory newCategory = new RootCategory();
					newCategory.setName(etCatEditName.getText().toString());
					newCategory.setType(type);
					newCategory.setIcon(selectedIcon);
					newCategory.setSubCategories(subCategories);
					newCategory.setId("rootcategory_"+UUID.randomUUID().toString());
					PocketAccounter.financeManager.getCategories().add(newCategory);
				}
				((PocketAccounter)getActivity()).replaceFragment(new CategoryFragment(), PockerTag.CATEGORY);
			}
			else if (type==PocketAccounterGeneral.INCOME) {
				RootCategory newCategory = new RootCategory();
				newCategory.setName(etCatEditName.getText().toString());
				newCategory.setType(type);
				newCategory.setIcon(selectedIcon);
				newCategory.setSubCategories(subCategories);
				newCategory.setId(categoryId);
				PocketAccounter.financeManager.getIncomes().set(pos, newCategory);
				PocketAccounter.financeManager.getCategories().add(newCategory);
				((PocketAccounter)getContext()).initialize(calendar);
				((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
			} else if (type==PocketAccounterGeneral.EXPENSE) {
				RootCategory newCategory = new RootCategory();
				newCategory.setName(etCatEditName.getText().toString());
				newCategory.setType(type);
				newCategory.setIcon(selectedIcon);
				newCategory.setSubCategories(subCategories);
				newCategory.setId(categoryId);
				PocketAccounter.financeManager.getExpanses().set(pos, newCategory);
				PocketAccounter.financeManager.getCategories().add(newCategory);
				((PocketAccounter)getContext()).initialize(calendar);
				((PocketAccounter)getContext()).getSupportFragmentManager().popBackStack();
			}
			PocketAccounter.financeManager.saveIncomes();
			PocketAccounter.financeManager.saveExpenses();
			PocketAccounter.financeManager.saveCategories();
			break;
		}
	}
	@SuppressLint({ "InfateParams", "NewApi" })
	public void openIconsDialog() {
		final Dialog dialog=new Dialog(getActivity());
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.cat_icon_select, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		final GridView gvCategoryIcons = (GridView) dialogView.findViewById(R.id.gvCategoryIcons);
		IconAdapterCategory adapter = new IconAdapterCategory(getActivity(), icons, selectedIcon);
		gvCategoryIcons.setAdapter(adapter);
		gvCategoryIcons.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedIcon = icons[position];
				int resId = getResources().getIdentifier(selectedIcon, "drawable", getContext().getPackageName());
				Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
				Bitmap icon = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp), (int)getResources().getDimension(R.dimen.twentyfive_dp), false);
				fabCatIcon.setImageBitmap(icon);
				dialog.dismiss();
			}
		});
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		dialog.getWindow().setLayout(8*width/9, LayoutParams.MATCH_PARENT);
		dialog.show();
	}
	@SuppressLint("InflateParams")
	private void openSubCatEditDialog(final SubCategory subCategory) {
		final Dialog dialog=new Dialog(getActivity());
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.sub_category_edit_layout, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		mode = PocketAccounterGeneral.NORMAL_MODE;
		setMode(mode);
		final FABIcon fabChooseIcon = (FABIcon) dialogView.findViewById(R.id.fabChooseIcon);
		Bitmap temp, scaled;
		if (subCategory != null)
			subcatIcon = subCategory.getIcon();
		else
			subcatIcon = "icons_4";
		int resId = getResources().getIdentifier(subcatIcon, "drawable", getContext().getPackageName());
		temp = BitmapFactory.decodeResource(getResources(), resId);
		scaled = Bitmap.createScaledBitmap(temp, (int) getResources().getDimension(R.dimen.twentyfive_dp), (int) getResources().getDimension(R.dimen.twentyfive_dp), false);
		fabChooseIcon.setImageBitmap(scaled);
		fabChooseIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog=new Dialog(getActivity());
				View dialogView = getActivity().getLayoutInflater().inflate(R.layout.cat_icon_select, null);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(dialogView);
				final GridView gvCategoryIcons = (GridView) dialogView.findViewById(R.id.gvCategoryIcons);
				IconAdapterCategory adapter = new IconAdapterCategory(getActivity(), icons, subcatIcon);
				gvCategoryIcons.setAdapter(adapter);
				gvCategoryIcons.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						int resId = getResources().getIdentifier(icons[position], "drawable", getContext().getPackageName());
						Bitmap temp  = BitmapFactory.decodeResource(getResources(), resId);
						Bitmap scaled = Bitmap.createScaledBitmap(temp, (int)getResources().getDimension(R.dimen.twentyfive_dp), (int)getResources().getDimension(R.dimen.twentyfive_dp), false);
						fabChooseIcon.setImageBitmap(scaled);
						subcatIcon = icons[position];
						dialog.dismiss();
					}
				});
				DisplayMetrics dm = getResources().getDisplayMetrics();
				int width = dm.widthPixels;
				dialog.getWindow().setLayout(8*width/9, LayoutParams.MATCH_PARENT);
				dialog.show();
			}
		});
		ImageView ivSubCatClose = (ImageView) dialogView.findViewById(R.id.ivSubCatClose);
		ImageView ivSubCatSave = (ImageView) dialogView.findViewById(R.id.ivSubCatSave);
		final EditText etSubCategoryName = (EditText) dialogView.findViewById(R.id.etSubCategoryName);
		if (subCategory != null)
			etSubCategoryName.setText(subCategory.getName());
		ivSubCatSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (etSubCategoryName.getText().toString().matches("")) {
					etSubCategoryName.setError(getResources().getString(R.string.sub_cat_name_error));
					return;
				}
				if (subCategory != null) {
					subCategory.setName(etSubCategoryName.getText().toString());
					subCategory.setIcon(subcatIcon);
				}
				else {
					SubCategory newSubCategory = new SubCategory();
					newSubCategory.setName(etSubCategoryName.getText().toString());
					newSubCategory.setId("subcat_"+UUID.randomUUID().toString());
					newSubCategory.setParent(categoryId);
					newSubCategory.setIcon(subcatIcon);
					subCategories.add(newSubCategory);
				}
				refreshSubCatList(PocketAccounterGeneral.NORMAL_MODE);
				dialog.dismiss();
			}
		});
		ivSubCatClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void setMode(int mode) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			ivSubCatDelete.setImageResource(R.drawable.subcat_delete);
			selected = null;
		}
		else {
			ivSubCatDelete.setImageResource(R.drawable.ic_cat_trash);
			selected = new boolean[subCategories.size()];
		}
		refreshSubCatList(mode);
	}
	private void deleteSubcats() {
		for (int i=0; i<selected.length; i++) {
			for (int j = 0; j<PocketAccounter.financeManager.getRecords().size(); j++) {
				if (PocketAccounter.financeManager.getRecords().get(j).getSubCategory() == null) continue;
				if (PocketAccounter.financeManager.getRecords().get(j).getSubCategory().getId().matches(subCategories.get(i).getId())) {
					PocketAccounter.financeManager.getRecords().get(j).setSubCategory(null);
				}
			}
			if (selected[i])
				subCategories.set(i, null);
		}
		for (int i=0; i<subCategories.size(); i++) {
			if (subCategories.get(i) == null) {
				subCategories.remove(i);
				i--;
			}
		}
	}
}