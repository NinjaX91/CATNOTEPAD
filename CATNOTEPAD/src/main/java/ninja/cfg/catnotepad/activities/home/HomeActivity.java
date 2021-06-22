package ninja.cfg.catnotepad.activities.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ninja.cfg.catnotepad.CatNotepad;
import ninja.cfg.catnotepad.R;
import ninja.cfg.catnotepad.SplashScreen;
import ninja.cfg.catnotepad.database.FoldersDAO;
import ninja.cfg.catnotepad.models.Folder;
import java.util.List;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;

/**
 * Created by MohMah on 8/17/2016.
 */
public class HomeActivity extends AppCompatActivity{

	private static final String TAG = "HomeActivity";
	private static final int ALL_NOTES_MENU_ID = -1;
	private static final int EDIT_FOLDERS_MENU_ID = -2;
	private static final int SAVE_DATABASE_MENU_ID = -3;
	private static final int IMPORT_DATABASE_MENU_ID = -4;
	private static final int Auth_Sign_Out_ID = 5;
	private MAMEnrollmentManager mEnrollmentManager;
	@BindView(R.id.navigation_view) NavigationView mNavigationView;
	@BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
	List<Folder> latestFolders;
	BackupRestoreDelegate backupRestoreDelegate;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);




		loadAccount();
		mEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);

		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);
		mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
			@Override public void onGlobalLayout(){
				mDrawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				setFragment(null);
			}
		});
		backupRestoreDelegate = new BackupRestoreDelegate(this);
		if (getIntent().getData() != null) backupRestoreDelegate.handleFilePickedWithIntentFilter(getIntent().getData());
			mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
				@Override public boolean onNavigationItemSelected(MenuItem item){
					Log.e(TAG, "onNavigationItemSelected() called with: " + "item id = [" + item.getItemId() + "]");
					int menuId = item.getItemId();
					if (menuId == ALL_NOTES_MENU_ID){
						setFragment(null);
					}else if (menuId == EDIT_FOLDERS_MENU_ID){
						//startActivity(new EditFoldersActivityIntentBuilder().build(HomeActivity.this));
					}else if (menuId == SAVE_DATABASE_MENU_ID){
						backupRestoreDelegate.backupDataToFile();
					}else if (menuId == IMPORT_DATABASE_MENU_ID){
						backupRestoreDelegate.startFilePickerIntent();
					}else if (menuId == Auth_Sign_Out_ID){
						signoutaction();

					}else{
						setFragment(FoldersDAO.getFolder(menuId));
					}
					mDrawerLayout.closeDrawer(Gravity.LEFT);
					inflateNavigationMenus(menuId);
					return true;
				}

			});

	}

	@Override protected void onStart(){
		super.onStart();
		inflateNavigationMenus(ALL_NOTES_MENU_ID);
	}

	public void inflateNavigationMenus(int checkedItemId){
		Menu menu = mNavigationView.getMenu();
		menu.clear();
		menu
				.add(Menu.NONE, ALL_NOTES_MENU_ID, Menu.NONE, "Notes")
				.setIcon(R.drawable.ic_note_white_24dp)
				.setChecked(checkedItemId == ALL_NOTES_MENU_ID);
		final SubMenu subMenu = menu.addSubMenu("Folders");
		latestFolders = FoldersDAO.getLatestFolders();
		for (Folder folder : latestFolders){
			subMenu
					.add(Menu.NONE, folder.getId(), Menu.NONE, folder.getName())
					.setIcon(R.drawable.ic_folder_black_24dp)
					.setChecked(folder.getId() == checkedItemId);
		}
		menu
				.add(Menu.NONE, EDIT_FOLDERS_MENU_ID, Menu.NONE, "Create or edit folders")
				.setIcon(R.drawable.ic_add_white_24dp);
		SubMenu backupSubMenu = menu.addSubMenu("Backup and restore");
		backupSubMenu
				.add(Menu.NONE, SAVE_DATABASE_MENU_ID, Menu.NONE, "Backup data")
				.setIcon(R.drawable.ic_save_white_24dp);
		backupSubMenu
				.add(Menu.NONE, IMPORT_DATABASE_MENU_ID, Menu.NONE, "Restore data")
				.setIcon(R.drawable.ic_restore_white_24dp);
		Menu authSubMenu = menu.addSubMenu("Authentication");
				authSubMenu
						.add (Menu.NONE, Auth_Sign_Out_ID, Menu.NONE,"Sign out")
						.setIcon(R.drawable.ic_action_format_dark);
	}

	@Override public void onBackPressed(){
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}else{
			super.onBackPressed();
		}
	}

	public void setFragment(Folder folder){
		// Create a new fragment and specify the fragment to show based on nav item clicked
		Fragment fragment = new NoteListFragment();
		if (folder != null){
			Bundle bundle = new Bundle();
			bundle.putParcelable(NoteListFragment.FOLDER, folder);
			fragment.setArguments(bundle);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BackupRestoreDelegate.PICK_RESTORE_FILE_REQUEST_CODE){
			backupRestoreDelegate.handleFilePickedWithFilePicker(resultCode, data);
		}
	}

	private final static String[] SCOPES = {"Files.Read"};
	/* Azure AD v2 Configs */
	final static String AUTHORITY = "https://login.microsoftonline.com/common";
	private ISingleAccountPublicClientApplication mSingleAccountApp;

	//private static final String TAG = HomeActivity.class.getSimpleName();

	/* UI & Debugging Variables */
	Button signInButton;
	Button signOutButton;
	Button callGraphApiInteractiveButton;
	Button callGraphApiSilentButton;
	TextView logTextView;
	TextView currentUserTextView;


	//When app comes to the foreground, load existing account to determine if user is signed in
	private void loadAccount() {
		if (mSingleAccountApp == null) {
			return;
		}

		mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
			@Override
			public void onAccountLoaded(@Nullable IAccount activeAccount) {
				// You can use the account data to update your UI or your app database.
				updateUI(activeAccount);
			}

			@Override
			public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
				if (currentAccount == null) {
					// Perform a cleanup task as the signed-in account changed.
					performOperationOnSignOut();
				}
			}

			@Override
			public void onError(@NonNull MsalException exception) {
				displayError(exception);
			}
		});
	}
	private void initializeUI(){
		signOutButton = findViewById(R.id.clearCache);
		currentUserTextView = findViewById(R.id.current_user);



		//Sign in user
		signInButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if (mSingleAccountApp == null) {
					return;
				}
				mSingleAccountApp.signIn(HomeActivity.this, null, SCOPES, getAuthInteractiveCallback());
				Intent mainIntent=new Intent(HomeActivity.this, CatNotepad.class);

				startActivity(mainIntent);

			}
		});
//sign out clear cache public sub



		//Sign out user
		signOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSingleAccountApp == null){
					return;
				}
				mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
					@Override
					public void onSignOut() {
						updateUI(null);
						performOperationOnSignOut();
					}
					@Override
					public void onError(@NonNull MsalException exception){
						displayError(exception);
					}
				});
			}
		});

		//Interactive
		callGraphApiInteractiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSingleAccountApp == null) {
					return;
				}
				mSingleAccountApp.acquireToken(HomeActivity.this, SCOPES, getAuthInteractiveCallback());
			}
		});

		//Silent
		callGraphApiSilentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSingleAccountApp == null){
					return;
				}
				mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
			}
		});
	}
	private AuthenticationCallback getAuthInteractiveCallback() {
		return new AuthenticationCallback() {
			@Override
			public void onSuccess(IAuthenticationResult authenticationResult) {
				/* Successfully got a token, use it to call a protected resource - MSGraph */
				Log.d(TAG, "Successfully authenticated");
				/* Update UI */
				updateUI(authenticationResult.getAccount());
				/* call graph */
				callGraphAPI(authenticationResult);
			}

			@Override
			public void onError(MsalException exception) {
				/* Failed to acquireToken */
				Log.d(TAG, "Authentication failed: " + exception.toString());
				displayError(exception);
			}
			@Override
			public void onCancel() {
				/* User canceled the authentication */
				Log.d(TAG, "User cancelled login.");
			}
		};
	}

	private SilentAuthenticationCallback getAuthSilentCallback() {
		return new SilentAuthenticationCallback() {
			@Override
			public void onSuccess(IAuthenticationResult authenticationResult) {
				Log.d(TAG, "Successfully authenticated");
				callGraphAPI(authenticationResult);
			}
			@Override
			public void onError(MsalException exception) {
				Log.d(TAG, "Authentication failed: " + exception.toString());
				displayError(exception);
			}
		};
	}
	private void callGraphAPI(IAuthenticationResult authenticationResult) {

		final String accessToken = authenticationResult.getAccessToken();

		IGraphServiceClient graphClient =
				GraphServiceClient
						.builder()
						.authenticationProvider(new IAuthenticationProvider() {
							@Override
							public void authenticateRequest(IHttpRequest request) {
								Log.d(TAG, "Authenticating request," + request.getRequestUrl());
								request.addHeader("Authorization", "Bearer " + accessToken);
							}
						})
						.buildClient();
		graphClient
				.me()
				.drive()
				.buildRequest()
				.get(new ICallback<Drive>() {
					@Override
					public void success(final Drive drive) {
						Log.d(TAG, "Found Drive " + drive.id);
						displayGraphResult(drive.getRawObject());
					}

					@Override
					public void failure(ClientException ex) {
						displayError(ex);
					}
				});
	}
	private void updateUI(@Nullable final IAccount account) {
		if (account != null) {
			signInButton.setEnabled(false);
			signOutButton.setEnabled(true);
			callGraphApiInteractiveButton.setEnabled(true);
			callGraphApiSilentButton.setEnabled(true);
			currentUserTextView.setText(account.getUsername());


		} else {
			signInButton.setEnabled(true);
			signOutButton.setEnabled(false);
			callGraphApiInteractiveButton.setEnabled(false);
			callGraphApiSilentButton.setEnabled(false);
			currentUserTextView.setText("");

			logTextView.setText("");
		}
	}
	private void displayError(@NonNull final Exception exception) {
		logTextView.setText(exception.toString());
	}
	private void displayGraphResult(@NonNull final JsonObject graphResponse) {
		logTextView.setText(graphResponse.toString());
	}
	private void performOperationOnSignOut() {
		final String signOutText = "Signed Out.";
		currentUserTextView.setText("");


	}
	private void signoutaction() {
		if (mSingleAccountApp == null) {
			return;
		}

		mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
			@Override
			public void onAccountLoaded(@Nullable IAccount activeAccount) {
				// You can use the account data to update your UI or your app database.
				updateUI(activeAccount);
			}

			@Override
			public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
				if (currentAccount == null) {
					// Perform a cleanup task as the signed-in account changed.
					performOperationOnSignOut();
				}
			}

			@Override
			public void onError(@NonNull MsalException exception) {
				displayError(exception);
			}
		});
	}
}
