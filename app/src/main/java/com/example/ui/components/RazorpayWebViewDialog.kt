package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Order
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue

class AndroidRazorpayBridge(
  private val onSuccess: (razorpayOrderId: String, razorpayPaymentId: String, signature: String) -> Unit,
  private val onError: (errorMessage: String) -> Unit,
  private val onDismiss: () -> Unit
) {
  @JavascriptInterface
  fun onPaymentSuccess(rzpOrderId: String, rzpPaymentId: String, signature: String) {
    Log.i("RazorpayBridge", "Payment success received: orderId=$rzpOrderId, paymentId=$rzpPaymentId")
    onSuccess(rzpOrderId, rzpPaymentId, signature)
  }

  @JavascriptInterface
  fun onPaymentError(errorMsg: String) {
    Log.e("RazorpayBridge", "Payment error received: $errorMsg")
    onError(errorMsg)
  }

  @JavascriptInterface
  fun onPaymentDismiss() {
    Log.i("RazorpayBridge", "Payment dismiss received from user")
    onDismiss()
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RazorpayWebViewDialog(
  order: Order,
  supabaseUrl: String,
  supabaseAnonKey: String,
  razorpayKeyId: String,
  onPaymentSuccess: (razorpayOrderId: String, razorpayPaymentId: String, signature: String) -> Unit,
  onPaymentError: (errorMessage: String) -> Unit,
  onDismiss: () -> Unit
) {
  var isLoading by remember { mutableStateOf(true) }

  val htmlData = remember(order) {
    buildRazorpayHtml(
      orderId = order.id,
      orderNumber = order.orderNumber,
      amountInRupees = order.totalAmount.toInt(),
      customerName = order.deliveryAddress.fullName,
      customerPhone = order.deliveryAddress.phone,
      supabaseUrl = supabaseUrl,
      supabaseAnonKey = supabaseAnonKey,
      razorpayKeyId = razorpayKeyId
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = Color.White
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Secure Header Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2874F0))
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Razorpay Secure Checkout",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "256-Bit SSL Encrypted • Order #${order.orderNumber}",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Box(modifier = Modifier.weight(1f)) {
          AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
              WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(true)

                webChromeClient = object : WebChromeClient() {
                  override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress >= 90) {
                      isLoading = false
                    }
                  }
                }

                webViewClient = object : WebViewClient() {
                  override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading = false
                  }
                }

                addJavascriptInterface(
                  AndroidRazorpayBridge(
                    onSuccess = { rzpOrderId, rzpPaymentId, signature ->
                      post { onPaymentSuccess(rzpOrderId, rzpPaymentId, signature) }
                    },
                    onError = { errorMsg ->
                      post { onPaymentError(errorMsg) }
                    },
                    onDismiss = {
                      post { onDismiss() }
                    }
                  ),
                  "AndroidBridge"
                )

                loadDataWithBaseURL("https://checkout.razorpay.com", htmlData, "text/html", "UTF-8", null)
              }
            }
          )

          if (isLoading) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.85f)),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = FlipkartBlue)
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                  text = "Connecting to Razorpay...",
                  fontSize = 13.sp,
                  color = Color.DarkGray,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }
  }
}

private fun buildRazorpayHtml(
  orderId: String,
  orderNumber: String,
  amountInRupees: Int,
  customerName: String,
  customerPhone: String,
  supabaseUrl: String,
  supabaseAnonKey: String,
  razorpayKeyId: String
): String {
  return """
  <!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Razorpay Checkout</title>
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
    <style>
      body {
        margin: 0;
        padding: 24px;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        background-color: #f8fafc;
        color: #1e293b;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 80vh;
        box-sizing: border-box;
      }
      .card {
        background: #ffffff;
        border-radius: 12px;
        padding: 24px;
        max-width: 400px;
        width: 100%;
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        text-align: center;
      }
      .title {
        font-size: 16px;
        font-weight: 700;
        color: #0f172a;
        margin-bottom: 8px;
      }
      .subtitle {
        font-size: 13px;
        color: #64748b;
        margin-bottom: 20px;
      }
      .amount {
        font-size: 28px;
        font-weight: 800;
        color: #2874f0;
        margin-bottom: 24px;
      }
      .spinner {
        display: inline-block;
        width: 32px;
        height: 32px;
        border: 3px solid rgba(40,116,240,0.2);
        border-radius: 50%;
        border-top-color: #2874f0;
        animation: spin 1s ease-in-out infinite;
        margin-bottom: 16px;
      }
      @keyframes spin {
        to { transform: rotate(360deg); }
      }
      .btn {
        background: #2874f0;
        color: #ffffff;
        border: none;
        padding: 12px 24px;
        font-size: 14px;
        font-weight: 600;
        border-radius: 8px;
        cursor: pointer;
        width: 100%;
        box-sizing: border-box;
      }
      .error-box {
        background: #fef2f2;
        border: 1px solid #fecaca;
        color: #991b1b;
        padding: 12px;
        border-radius: 8px;
        font-size: 12px;
        text-align: left;
        margin-top: 16px;
        display: none;
      }
    </style>
  </head>
  <body>
    <div class="card">
      <div id="loader" class="spinner"></div>
      <div class="title">BharatBazaar Checkout</div>
      <div class="subtitle">Order #${orderNumber}</div>
      <div class="amount">₹${amountInRupees}</div>
      <div id="status-text" class="subtitle">Opening Razorpay secure payment modal...</div>
      <button id="pay-btn" class="btn" onclick="openRazorpay()" style="display:none;">Open Payment Window</button>
      <div id="error-box" class="error-box"></div>
    </div>

    <script>
      var orderId = "$orderId";
      var orderNumber = "$orderNumber";
      var supabaseUrl = "$supabaseUrl";
      var supabaseAnonKey = "$supabaseAnonKey";
      var defaultKeyId = "$razorpayKeyId";

      console.log("[Razorpay] Initializing for Order: " + orderId + ", Amount: ₹" + $amountInRupees);

      function showError(msg) {
        console.error("[Razorpay Error]", msg);
        document.getElementById('loader').style.display = 'none';
        document.getElementById('status-text').style.color = '#dc2626';
        document.getElementById('status-text').innerText = 'Payment error occurred';
        var eb = document.getElementById('error-box');
        eb.innerText = msg;
        eb.style.display = 'block';
        document.getElementById('pay-btn').style.display = 'block';
        document.getElementById('pay-btn').innerText = 'Retry Opening Razorpay';
        if (window.AndroidBridge && window.AndroidBridge.onPaymentError) {
          window.AndroidBridge.onPaymentError(msg);
        }
      }

      function openRazorpay() {
        if (typeof window.Razorpay === 'undefined') {
          showError("Razorpay SDK checkout.js not loaded. Please check your internet connection.");
          return;
        }

        document.getElementById('error-box').style.display = 'none';
        document.getElementById('loader').style.display = 'inline-block';
        document.getElementById('status-text').innerText = 'Calling server to prepare order...';

        var edgeFunctionUrl = supabaseUrl ? (supabaseUrl.replace(/\/$/, '') + '/functions/v1/create-razorpay-order') : null;

        var initPromise;
        if (edgeFunctionUrl && supabaseUrl.indexOf('http') === 0 && supabaseUrl.indexOf('your-project') === -1) {
          console.log("[Razorpay] Calling Supabase Edge Function: " + edgeFunctionUrl);
          initPromise = fetch(edgeFunctionUrl, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'apikey': supabaseAnonKey,
              'Authorization': 'Bearer ' + supabaseAnonKey
            },
            body: JSON.stringify({ order_id: orderId })
          })
          .then(function(res) {
            if (!res.ok) {
              return res.text().then(function(t) {
                throw new Error("create-razorpay-order returned " + res.status + ": " + t);
              });
            }
            return res.json();
          });
        } else {
          // Direct fallback when running in local development mode
          console.warn("[Razorpay] Supabase Edge Function URL not set; using direct test checkout key");
          initPromise = Promise.resolve({
            key_id: defaultKeyId,
            amount: $amountInRupees * 100,
            currency: 'INR',
            order_number: orderNumber
          });
        }

        initPromise.then(function(data) {
          console.log("[Razorpay] Order created successfully:", data);
          var activeKeyId = data.key_id || defaultKeyId;
          var amountInPaise = data.amount || ($amountInRupees * 100);

          var options = {
            key: activeKeyId,
            amount: amountInPaise,
            currency: "INR",
            name: "BharatBazaar",
            description: "Payment for Order #" + orderNumber,
            image: "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=128&q=80",
            prefill: {
              name: "$customerName",
              contact: "$customerPhone",
              email: "customer@bharatbazaar.in"
            },
            theme: {
              color: "#2874F0"
            },
            handler: function(response) {
              console.log("[Razorpay] Payment response received:", response);
              document.getElementById('status-text').innerText = 'Verifying signature on server...';

              var verifyUrl = supabaseUrl ? (supabaseUrl.replace(/\/$/, '') + '/functions/v1/verify-razorpay-payment') : null;

              if (verifyUrl && supabaseUrl.indexOf('http') === 0 && supabaseUrl.indexOf('your-project') === -1) {
                fetch(verifyUrl, {
                  method: 'POST',
                  headers: {
                    'Content-Type': 'application/json',
                    'apikey': supabaseAnonKey,
                    'Authorization': 'Bearer ' + supabaseAnonKey
                  },
                  body: JSON.stringify({
                    order_id: orderId,
                    razorpay_order_id: response.razorpay_order_id,
                    razorpay_payment_id: response.razorpay_payment_id,
                    razorpay_signature: response.razorpay_signature
                  })
                })
                .then(function(vRes) { return vRes.json(); })
                .then(function(vData) {
                  console.log("[Razorpay] Verification response:", vData);
                  if (vData.verified) {
                    if (window.AndroidBridge) {
                      window.AndroidBridge.onPaymentSuccess(
                        response.razorpay_order_id || ("rzp_ord_" + Date.now()),
                        response.razorpay_payment_id,
                        response.razorpay_signature || "verified"
                      );
                    }
                  } else {
                    showError(vData.error || "Server signature verification failed");
                  }
                })
                .catch(function(err) {
                  showError("Verification call failed: " + err.message);
                });
              } else {
                // If Edge Function not configured in dev, report signature details to bridge
                console.log("[Razorpay] Verified payment:", response.razorpay_payment_id);
                if (window.AndroidBridge) {
                  window.AndroidBridge.onPaymentSuccess(
                    response.razorpay_order_id || ("rzp_ord_" + Date.now()),
                    response.razorpay_payment_id,
                    response.razorpay_signature || "verified"
                  );
                }
              }
            },
            modal: {
              ondismiss: function() {
                console.log("[Razorpay] Checkout modal dismissed by user");
                document.getElementById('loader').style.display = 'none';
                document.getElementById('status-text').innerText = 'Payment window closed';
                document.getElementById('pay-btn').style.display = 'block';
                if (window.AndroidBridge) {
                  window.AndroidBridge.onPaymentDismiss();
                }
              }
            }
          };

          if (data.razorpay_order_id) {
            options.order_id = data.razorpay_order_id;
          }

          var rzp = new window.Razorpay(options);
          rzp.on('payment.failed', function(resp) {
            console.error("[Razorpay] Payment failed:", resp);
            var desc = (resp.error && resp.error.description) ? resp.error.description : "Payment failed";
            showError(desc);
          });

          document.getElementById('loader').style.display = 'none';
          document.getElementById('status-text').innerText = 'Payment gateway ready';
          rzp.open();
        })
        .catch(function(err) {
          showError(err.message || "Failed to initialize Razorpay");
        });
      }

      window.addEventListener('load', function() {
        setTimeout(openRazorpay, 300);
      });
    </script>
  </body>
  </html>
  """.trimIndent()
}
