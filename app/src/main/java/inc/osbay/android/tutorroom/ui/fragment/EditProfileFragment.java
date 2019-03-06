package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.adapter.CountryCodeAdapter;
import inc.osbay.android.tutorroom.adapter.LanguageAdapter;
import inc.osbay.android.tutorroom.sdk.client.ServerError;
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager;
import inc.osbay.android.tutorroom.sdk.client.ServerResponse;
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant;
import inc.osbay.android.tutorroom.sdk.database.DBAdapter;
import inc.osbay.android.tutorroom.sdk.model.Account;
import inc.osbay.android.tutorroom.sdk.model.CountryCode;
import inc.osbay.android.tutorroom.sdk.model.Language;
import inc.osbay.android.tutorroom.sdk.util.LGCUtil;
import inc.osbay.android.tutorroom.ui.activity.CameraActivity;
import inc.osbay.android.tutorroom.utils.ImageFilePath;
import inc.osbay.android.tutorroom.utils.SharedPreferenceData;

public class EditProfileFragment extends BackHandledFragment {

    public static final String TEMP_PHOTO_URL = CommonConstant.IMAGE_PATH + File.separator + "temp.jpg";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int TAKE_PICTURE = 111;
    private static final int CHOOSE_PICTURE = 112;
    @BindView(R.id.country_code_spinner)
    Spinner mSpnAccountPhCodes;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.name_ed)
    EditText nameTv;
    @BindView(R.id.email_ed)
    EditText emailTv;
    @BindView(R.id.phone_ed)
    EditText phoneTV;
    @BindView(R.id.country_ed)
    EditText countryTv;
    @BindView(R.id.address_ed)
    EditText addressTv;
    @BindView(R.id.native_lang_spinner)
    Spinner nativeLangTv;
    @BindView(R.id.confirm_tv)
    TextView confirmTv;
    @BindView(R.id.sdv_profile_photo)
    SimpleDraweeView profilePic;
    SharedPreferenceData sharedPreferenceData;
    private ProgressDialog progressDialog;
    private ServerRequestManager mRequestManager;
    private Account mAccount;
    private CountryCodeAdapter countryCodeAdapter;
    private String accountId;
    private List<Language> languageList = new ArrayList<>();
    private LanguageAdapter languageAdapter;
    private DBAdapter mDBAdapter;
    private String nativeLang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceData = new SharedPreferenceData(Objects.requireNonNull(getActivity()));
        mRequestManager = new ServerRequestManager(getActivity());

        mDBAdapter = new DBAdapter(getActivity());
        accountId = String.valueOf(sharedPreferenceData.getInt("account_id"));
        mAccount = mDBAdapter.getAccountById(accountId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        mRequestManager.getLanguageList(new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse response) {
                progressDialog.dismiss();
                if (response.getCode() == 1) {
                    try {
                        JSONArray languageArray = new JSONArray(response.getDataSt());
                        for (int i = 0; i < languageArray.length(); i++) {
                            Language language = new Language(languageArray.getJSONObject(i));
                            languageList.add(language);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    languageAdapter = new LanguageAdapter(getActivity(), languageList);
                    List<CountryCode> mCountryCodes = mDBAdapter.getCountryCodes();
                    countryCodeAdapter = new CountryCodeAdapter(getActivity(), mCountryCodes);

                    mSpnAccountPhCodes.setAdapter(countryCodeAdapter);
                    nativeLangTv.setAdapter(languageAdapter);
                    profilePic.setImageURI(Uri.parse(mAccount.getAvatar()));
                    nameTv.setText(mAccount.getName());
                    emailTv.setText(mAccount.getEmail());
                    for (int i = 0; i < mSpnAccountPhCodes.getAdapter().getCount(); i++) {
                        if (mSpnAccountPhCodes.getAdapter().getItem(i).toString().contains(mAccount.getPhoneCode())) {
                            mSpnAccountPhCodes.setSelection(i);
                            break;
                        }
                    }
                    phoneTV.setText(mAccount.getPhoneNumber());

                    if (!mAccount.getCountry().equals("null") && !mAccount.getCountry().equals(""))
                        countryTv.setText(mAccount.getCountry());
                    else
                        countryTv.setText("");
                    if (!mAccount.getAddress().equals("null") && !mAccount.getAddress().equals(""))
                        addressTv.setText(mAccount.getAddress());
                    else
                        addressTv.setText("");

                    if (!mAccount.getSpeakingLang().equals("null") && !mAccount.getSpeakingLang().equals("")) {
                        for (int i = 0; i < nativeLangTv.getAdapter().getCount(); i++) {
                            if (nativeLangTv.getAdapter().getItem(i).toString().equalsIgnoreCase(mAccount.getSpeakingLang())) {
                                nativeLangTv.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        nativeLangTv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nativeLang = languageList.get(i).getLanguageName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @OnClick(R.id.confirm_tv)
    void clickSaveProfile() {
        progressDialog.show();

        String name = nameTv.getText().toString();
        String email = emailTv.getText().toString();
        String countryCodeSt = mSpnAccountPhCodes.getSelectedItem().toString();
        String phone = phoneTV.getText().toString();
        String country = countryTv.getText().toString();
        String address = addressTv.getText().toString();

        mRequestManager.updateProfileInfo(accountId, name, countryCodeSt, phone, email, address,
                country, nativeLang, new ServerRequestManager.OnRequestFinishedListener() {
                    @Override
                    public void onSuccess(ServerResponse result) {
                        getStudentInfo(accountId, progressDialog);
                    }

                    @Override
                    public void onError(ServerError err) {
                        progressDialog.dismiss();
                        Log.i("Update Failed", err.getMessage());
                    }
                });
    }

    private void getStudentInfo(String studentID, ProgressDialog progressDialog) {
        mRequestManager.getProfileInfo(studentID, new ServerRequestManager.OnRequestFinishedListener() {
            @Override
            public void onSuccess(ServerResponse result) {
                progressDialog.dismiss();
                getFragmentManager().popBackStack();
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                Log.i("Profile Failed", err.getMessage());
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.profile));
        setDisplayHomeAsUpEnable(true);
        setHasOptionsMenu(true);
    }

    @OnClick(R.id.profile_pic_rl)
    void clickProfilePic() {
        showChoosePhotoDialog();
    }

    private void showChoosePhotoDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_photo);

        TextView cameraButton = dialog.findViewById(R.id.tv_camera);
        cameraButton.setOnClickListener(arg0 -> {
            // start default camera
            Intent cameraIntent = new Intent(getActivity(),
                    CameraActivity.class);
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                    TEMP_PHOTO_URL);
            startActivityForResult(cameraIntent, TAKE_PICTURE);
            dialog.dismiss();
        });

        TextView galleryButton = dialog.findViewById(R.id.tv_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setType("image/jpeg");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            getString(R.string.ac_info_choose)),
                            CHOOSE_PICTURE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, CHOOSE_PICTURE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Map<String, String> params = new HashMap<>();

        switch (requestCode) {
            case CHOOSE_PICTURE:
                Uri originalUri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    originalUri = data.getData();
                } else {
                    originalUri = data.getData();
                    // Check for the freshest data.
                    getActivity().getContentResolver()
                            .takePersistableUriPermission(originalUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                try {
                    Log.e("Image URI - ", originalUri.toString());
                    LGCUtil.copyFile(
                            ImageFilePath.getPath(getActivity(),
                                    originalUri),
                            TEMP_PHOTO_URL);

                    CropImage.activity(Uri.fromFile(new File(TEMP_PHOTO_URL)))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(getActivity());

                } catch (Exception e) {
                    Log.e(TAG, "Photo unreadable - ", e);
                    Toast.makeText(getActivity(),
                            getString(R.string.ac_no_photo_url),
                            Toast.LENGTH_SHORT).show();
                }
                return;
            case TAKE_PICTURE:
                CropImage.activity(Uri.fromFile(new File(TEMP_PHOTO_URL)))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getActivity());
                return;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                params.put("Field", "Photo");

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri croppedUri = result.getUri();

                try {
                    Log.e("Cropped URI - ", croppedUri.toString());
                    LGCUtil.copyFile(
                            ImageFilePath.getPath(getActivity(),
                                    croppedUri),
                            TEMP_PHOTO_URL);

                    if (uploadImageToServer()) return;

                } catch (Exception e) {
                    Log.e(TAG, "Photo unreadable - ", e);
                    Toast.makeText(getActivity(),
                            getString(R.string.ac_no_photo_url),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return;
        }
    }

    private boolean uploadImageToServer() {
        int fixValue = 300;
        int widthValue;
        int heightValue;

        Bitmap bmp = LGCUtil
                .readImageFile(TEMP_PHOTO_URL);
        if (bmp == null) {
            Toast.makeText(getActivity(),
                    getString(R.string.ac_no_photo_url),
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (bmp.getWidth() > bmp.getHeight()) {
            float ratio = (float) bmp.getHeight()
                    / (float) bmp.getWidth();
            heightValue = (int) (fixValue * ratio);
            widthValue = fixValue;
        } else {
            float ratio = (float) bmp.getWidth()
                    / (float) bmp.getHeight();
            widthValue = (int) (fixValue * ratio);
            heightValue = fixValue;
        }

        Bitmap scaled = Bitmap.createScaledBitmap(bmp,
                widthValue,
                heightValue, true);

        LGCUtil.saveImageFile(scaled, TEMP_PHOTO_URL);

        uploadImage(scaled, TEMP_PHOTO_URL);

        bmp.recycle();
        return false;
    }

    private void uploadImage(Bitmap scaled, final String imgPath) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        mRequestManager.updateAvatar(String.valueOf(mAccount.getAccountId()), imageString, new ServerRequestManager.OnRequestFinishedListener() {

            @Override
            public void onSuccess(ServerResponse obj) {
                progressDialog.dismiss();
                if (getActivity() != null) {
                    if (obj != null) {
                        mAccount.setAvatar(obj.toString());

                        try {
                            changeProfileImage();
                        } catch (Exception e) {
                            Log.e(TAG, "Cannot read Image file", e);
                        }
                    } else {
                        Log.e(TAG,
                                "Update Image link is null");
                    }
                }
            }

            @Override
            public void onError(ServerError err) {
                progressDialog.dismiss();
                if (getActivity() != null) {
                    if (!TextUtils.isEmpty(mAccount.getAvatar())) {
                        changeProfileImage();
                    }
                }
            }
        });
    }

    private void changeProfileImage() {
        if (!TextUtils.isEmpty(mAccount.getAvatar())) {
            profilePic.setImageURI(Uri.parse(mAccount.getAvatar()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
