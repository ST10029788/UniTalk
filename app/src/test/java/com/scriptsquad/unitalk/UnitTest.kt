package com.scriptsquad.unitalk

//
//@RunWith(MockitoJUnitRunner::class)
//class AccountActivityTest {
//
//    private lateinit var accountActivity: Account_Activity
//    private lateinit var binding: ActivityAccountBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var progressDialog: ProgressDialog
//
//    @Before
//    fun setUp() {
//        accountActivity = Account_Activity()
//        binding = mock(ActivityAccountBinding::class.java)
//        firebaseAuth = mock(FirebaseAuth::class.java)
//        progressDialog = mock(ProgressDialog::class.java)
//
//        // Mocking Firebase Auth instance
//        `when`(firebaseAuth.currentUser).thenReturn(mock())
//        `when`(accountActivity.firebaseAuth).thenReturn(firebaseAuth)
//
//        // Simulating Activity Creation
//        accountActivity.onCreate(mock(Bundle::class.java))
//        accountActivity.binding = binding
//    }
//
//    @Test
//    fun testDarkModeSwitchEnabled() {
//        val sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
//            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("isDarkModeOn", true)
//        editor.apply()
//
//        accountActivity.onCreate(mock(Bundle::class.java))
//
//        assert(binding.darkModeSwitch.isChecked)
//        assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
//    }
//
//    @Test
//    fun testLogout() {
//        accountActivity.logoutCv.performClick()
//
//        verify(firebaseAuth).signOut()
//        val intentCaptor = ArgumentCaptor.forClass(Intent::class.java)
//        verify(accountActivity).startActivity(intentCaptor.capture())
//        assert(intentCaptor.value.component?.className == Log_In_Screen::class.java.name)
//        verify(accountActivity).finish()
//    }
//
//    @Test
//    fun testVerifyAccount() {
//        accountActivity.verifyAccount()
//
//        verify(progressDialog).setMessage("Sending verification link to email:")
//        verify(progressDialog).show()
//
//        // Mock the behavior of sendEmailVerification and check for success
//        val user = mock(FirebaseUser::class.java)
//        `when`(firebaseAuth.currentUser).thenReturn(user)
//        `when`(user.sendEmailVerification()).thenReturn(mock(Task::class.java))
//
//        accountActivity.verifyAccount()
//
//        // Verify that the progress dialog is dismissed after sending
//        verify(progressDialog).dismiss()
//        verify(Utils).toast(any(), any())
//    }
//
//    // More tests can be added to cover other functionalities like loading user info
//}
