package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val loggedInPhone by viewModel.loggedInUserPhone.collectAsState()
  val isAuthLoading by viewModel.isAuthLoading.collectAsState()
  val authError by viewModel.authError.collectAsState()
  val otpSent by viewModel.otpSent.collectAsState()
  val isDemoOtp by viewModel.isDemoOtp.collectAsState()

  var phoneNumber by remember { mutableStateOf("") }
  var otpCode by remember { mutableStateOf("") }
  val focusManager = LocalFocusManager.current

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (otpSent) "Verify OTP" else "Login / Sign Up",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = {
              if (otpSent) {
                viewModel.resetAuthState()
              } else {
                viewModel.navigateBack()
              }
            },
            modifier = Modifier.testTag("login_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = FlipkartBlue)
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF1F2F4))
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .testTag("login_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header Banner Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(FlipkartBlue)
          .padding(horizontal = 24.dp, vertical = 20.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "BharatBazaar mein Swagat Hai!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              )
              Text(
                text = "Orders, Wishlist & Offers ke liye login karein",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Already logged in state card
      if (loggedInPhone != null) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color(0xFF2E7D32),
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Aap Already Logged In Hain!",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Mobile Number: $loggedInPhone",
              fontSize = 14.sp,
              color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
              onClick = { viewModel.logout() },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button")
            ) {
              Text("Logout Karein", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        // Main Auth Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp)
          ) {
            if (!otpSent) {
              // STEP 1: Phone Number Input (10 Digits)
              Text(
                text = "Apna Mobile Number Enter Karein",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Verification ke liye 6-digit OTP code SMS par aayega",
                fontSize = 12.sp,
                color = Color.Gray
              )

              Spacer(modifier = Modifier.height(18.dp))

              OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                  if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                    phoneNumber = it
                  }
                },
                leadingIcon = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                  ) {
                    Text(
                      text = "🇮🇳 +91",
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .background(Color.LightGray)
                    )
                  }
                },
                placeholder = { Text("10-digit mobile number", fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.NumberPassword,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    focusManager.clearFocus()
                    if (phoneNumber.length == 10) {
                      viewModel.sendOtp(phoneNumber)
                    }
                  }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = FlipkartBlue,
                  unfocusedBorderColor = Color(0xFFCCCCCC)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("phone_input_field")
              )

              if (authError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = authError ?: "",
                  color = Color.Red,
                  fontSize = 12.sp
                )
              }

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = {
                  focusManager.clearFocus()
                  viewModel.sendOtp(phoneNumber)
                },
                enabled = phoneNumber.length == 10 && !isAuthLoading,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFFF9f00),
                  contentColor = Color.White,
                  disabledContainerColor = Color(0xFFFF9f00).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("send_otp_button")
              ) {
                if (isAuthLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text("OTP Bhej Rahe Hain...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                  Text("Continue / OTP Praapt Karein", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                Text(
                  text = "  YA TEST LOGIN  ",
                  fontSize = 11.sp,
                  color = Color.Gray,
                  fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
              }

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedButton(
                onClick = {
                  focusManager.clearFocus()
                  val num = if (phoneNumber.length == 10) phoneNumber else "9876543210"
                  viewModel.quickDemoLogin(num)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(46.dp)
                  .testTag("quick_demo_login_button"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.2.dp, FlipkartBlue)
              ) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = FlipkartBlue,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "One-Tap Demo Login (Bina SMS/OTP)",
                  color = FlipkartBlue,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = "Continue par click karke aap BharatBazaar ke Terms & Privacy Policy se sahmat hote hain.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              // STEP 2: OTP Verification Input (6 Digits)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "OTP Verify Karein",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1E293B)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "+91 $phoneNumber par 6-digit OTP bheja gaya hai",
                    fontSize = 12.sp,
                    color = Color.Gray
                  )
                }
                TextButton(
                  onClick = {
                    viewModel.resetAuthState()
                    otpCode = ""
                  }
                ) {
                  Text("Change", color = FlipkartBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }

              if (isDemoOtp) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                  color = Color(0xFFEFF6FF),
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "ℹ️ Test Mode (Supabase SMS provider pending)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E40AF)
                      )
                      Text(
                        text = "Test OTP: 123456",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlipkartBlue
                      )
                    }
                    TextButton(
                      onClick = { otpCode = "123456" },
                      modifier = Modifier.testTag("fill_test_otp_button")
                    ) {
                      Text("Fill 123456", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FlipkartBlue)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              OutlinedTextField(
                value = otpCode,
                onValueChange = {
                  if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    otpCode = it
                  }
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = FlipkartBlue
                  )
                },
                placeholder = { Text("6-digit OTP darj karein", fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.NumberPassword,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    focusManager.clearFocus()
                    if (otpCode.length == 6) {
                      viewModel.verifyOtp(phoneNumber, otpCode)
                    }
                  }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = FlipkartBlue,
                  unfocusedBorderColor = Color(0xFFCCCCCC)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("otp_input_field")
              )

              if (authError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = authError ?: "",
                  color = Color.Red,
                  fontSize = 12.sp
                )
              }

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = {
                  focusManager.clearFocus()
                  viewModel.verifyOtp(phoneNumber, otpCode)
                },
                enabled = otpCode.length == 6 && !isAuthLoading,
                colors = ButtonDefaults.buttonColors(
                  containerColor = FlipkartBlue,
                  contentColor = Color.White,
                  disabledContainerColor = FlipkartBlue.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("verify_otp_button")
              ) {
                if (isAuthLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text("Verify Ho Raha Hai...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                  Text("Verify OTP & Login", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "OTP nahi mila? ",
                  fontSize = 12.sp,
                  color = Color.Gray
                )
                TextButton(
                  onClick = {
                    viewModel.sendOtp(phoneNumber)
                  },
                  enabled = !isAuthLoading
                ) {
                  Text(
                    text = "Resend OTP",
                    color = FlipkartBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Trust Badges
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("100% Secure Login", fontSize = 11.sp, color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Verified via Supabase", fontSize = 11.sp, color = Color.Gray)
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
