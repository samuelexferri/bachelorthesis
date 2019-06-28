new AsyncTask<Void, Void, Void>() {

    public Void doInBackground(Void... params) {
        // URI BUILDER
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority(DefaultValues.AUTHORITY)
                .appendPath("se4medservice")
                .appendPath("")
                .appendQueryParameter(DefaultValues.ACTION_PARAM_NAME, DefaultValues.DELETEPATIENTDOC_ACTION)
                .appendQueryParameter(DefaultValues.EMAIL_PARAM, doctorMail)
                .appendQueryParameter(DefaultValues.PASSWORD_PARAM, doctorPassword)
                .appendQueryParameter(DefaultValues.IDPATIENT_PARAM, String.valueOf(idPatient))
                .appendQueryParameter(DefaultValues.IDAPP_PARAM, DefaultValues.STEREOTEST); // Insert semicolon

        String myUri = builder.build().toString();
        System.out.println("URI: " + myUri);

        // URL CONNECTION
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(myUri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            System.out.println("RESPONSE CODE: " + responseCode);

            if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED)) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } else {
                // Toast (Handler)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(), "Network error (HTTP)", Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            }
        } catch (Exception e) {
            // Toast (Handler)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast t = Toast.makeText(getApplicationContext(), "Network error (E)", Toast.LENGTH_LONG);
                    t.show();
                }
            });
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        try {
            String response = "{" + result.toString().substring(result.toString().indexOf("{") + 1, result.toString().lastIndexOf("}")) + "}";
            System.out.println("RESPONSE: " + response);

            JSONObject jsonobject = new JSONObject(response);
            final String status = jsonobject.getString("status");
            System.out.println("STATUS: " + status);

            if (status.equals(DefaultValues.STATUS_OK)) {
                SharedPreferences.Editor e = SPPatient.edit();
                e.putString(DefaultValues.ACTUAL_PATIENT_NAME, "404");
                e.putString(DefaultValues.ACTUAL_PATIENT_SURNAME, "404");
                e.putInt(DefaultValues.ACTUAL_PATIENT_ID, -1);
                e.apply();

                // Toast (Handler)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(), "Patient deleted", Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            } else {
                // Toast (Handler)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(), "Status: " + status, Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }