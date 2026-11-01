package com.example.ui.util

import com.example.data.model.AppLanguage

object Strings {
  fun appTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "All Calculator"
    AppLanguage.ENGLISH -> "All Calculator"
  }

  fun appSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "রিওয়ার্ড, সাধারণ ও বয়স ক্যালকুলেটর"
    AppLanguage.ENGLISH -> "Reward, Standard & Age Calculators"
  }

  fun inputSectionTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাবের তথ্য দিন"
    AppLanguage.ENGLISH -> "Enter Calculation Inputs"
  }

  fun principalLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রাথমিক টাকা / মূলধন"
    AppLanguage.ENGLISH -> "Initial Principal / Investment"
  }

  fun rateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দৈনিক রিওয়ার্ড (%)"
    AppLanguage.ENGLISH -> "Daily Reward (%)"
  }

  fun daysLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট দিন"
    AppLanguage.ENGLISH -> "Total Duration (Days)"
  }

  fun currencyLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মুদ্রা"
    AppLanguage.ENGLISH -> "Currency"
  }

  fun compoundCheck(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রতিদিনের রিওয়ার্ড মূলধনে যোগ করে কম্পাউন্ড হবে"
    AppLanguage.ENGLISH -> "Reinvest daily reward into principal (Compounding)"
  }

  fun simpleCheck(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রতিদিনের একই রিওয়ার্ডের টাকা মূলধন থেকে আলাদা রাখব (কম্পাউন্ড বন্ধ)"
    AppLanguage.ENGLISH -> "Keep daily rewards separate (Disable compounding)"
  }

  fun advancedOptions(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "উন্নত সেটিংস (দৈনিক অতিরিক্ত জমা)"
    AppLanguage.ENGLISH -> "Advanced Settings (Daily Additional Deposit)"
  }

  fun dailyAdditionLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রতিদিন অতিরিক্ত জমা (ঐচ্ছিক)"
    AppLanguage.ENGLISH -> "Daily Additional Deposit (Optional)"
  }

  fun calculateButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব করুন"
    AppLanguage.ENGLISH -> "Calculate"
  }

  fun downloadCsvButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "CSV রিপোর্ট ডাউনলোড ও শেয়ার"
    AppLanguage.ENGLISH -> "Download & Share CSV Report"
  }

  fun copySummaryButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সারাংশ কপি করুন"
    AppLanguage.ENGLISH -> "Copy Summary"
  }

  fun shareSummaryButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শেয়ার করুন"
    AppLanguage.ENGLISH -> "Share Summary"
  }

  fun saveScenarioButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব সেভ করুন"
    AppLanguage.ENGLISH -> "Save Scenario"
  }

  fun savedScenariosButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সংরক্ষিত হিসাব"
    AppLanguage.ENGLISH -> "Saved Scenarios"
  }

  fun resultsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ফলাফল"
    AppLanguage.ENGLISH -> "Calculation Results"
  }

  fun finalBalance(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শেষের মোট মূল্য"
    AppLanguage.ENGLISH -> "Ending Balance"
  }

  fun totalProfit(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট লাভ / রিওয়ার্ড"
    AppLanguage.ENGLISH -> "Total Profit / Rewards"
  }

  fun initialPrincipal(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রাথমিক মূলধন"
    AppLanguage.ENGLISH -> "Initial Principal"
  }

  fun totalReturn(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট রিটার্ন"
    AppLanguage.ENGLISH -> "Total Return"
  }

  fun dailyAverage(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দৈনিক গড় লাভ"
    AppLanguage.ENGLISH -> "Daily Average"
  }

  fun compoundBoost(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "কম্পাউন্ডিংয়ের ফলে অতিরিক্ত লাভ"
    AppLanguage.ENGLISH -> "Extra Gain from Compounding"
  }

  fun breakdownTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দিন অনুযায়ী হিসাব"
    AppLanguage.ENGLISH -> "Day-by-Day Breakdown"
  }

  fun chartTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ব্যালান্স বৃদ্ধির গ্রাফ"
    AppLanguage.ENGLISH -> "Growth Projection Chart"
  }

  fun dayHeader(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দিন"
    AppLanguage.ENGLISH -> "Day"
  }

  fun openingHeader(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শুরুর ব্যালান্স"
    AppLanguage.ENGLISH -> "Opening Balance"
  }

  fun rewardHeader(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "রিওয়ার্ড"
    AppLanguage.ENGLISH -> "Reward"
  }

  fun closingHeader(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শেষের ব্যালান্স"
    AppLanguage.ENGLISH -> "Closing Balance"
  }

  fun cumulativeHeader(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট সঞ্চিত"
    AppLanguage.ENGLISH -> "Cumulative"
  }

  fun disclaimerNote(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এটি গাণিতিক অনুমান। বাস্তব রিওয়ার্ড, ফি, ট্যাক্স, মূল্য পরিবর্তন বা টাকা তোলার সীমা এতে ধরা হয়নি।"
    AppLanguage.ENGLISH -> "This is a mathematical simulation. Real-world fees, taxes, token price fluctuations, or withdrawal restrictions are not included."
  }

  fun footerNote(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "All Calculator • অফলাইনে কাজ করে • বিনিয়োগের নিশ্চয়তা নয়"
    AppLanguage.ENGLISH -> "All Calculator • Works Offline • Not Financial Advice"
  }

  // Navigation Tabs
  fun tabRewardCompound(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "রিওয়ার্ড"
    AppLanguage.ENGLISH -> "Reward"
  }

  fun tabNormalCalc(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ক্যালকুলেটর"
    AppLanguage.ENGLISH -> "Calculator"
  }

  fun tabAgeCalc(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বয়স হিসাব"
    AppLanguage.ENGLISH -> "Age Calc"
  }

  // Normal Calculator
  fun normalCalcTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সাধারণ ক্যালকুলেটর"
    AppLanguage.ENGLISH -> "Standard Calculator"
  }

  fun normalCalcSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সহজ ও বৈজ্ঞানিক গাণিতিক হিসাব"
    AppLanguage.ENGLISH -> "Simple & Scientific Arithmetic"
  }

  fun historyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাবের ইতিহাস"
    AppLanguage.ENGLISH -> "Calculation History"
  }

  fun clearHistory(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ইতিহাস মুছুন"
    AppLanguage.ENGLISH -> "Clear History"
  }

  fun noHistory(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এখনও কোনো ইতিহাস নেই"
    AppLanguage.ENGLISH -> "No calculations yet"
  }

  fun loadExpression(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব লোড"
    AppLanguage.ENGLISH -> "Load Formula"
  }

  fun loadResult(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ফল লোড"
    AppLanguage.ENGLISH -> "Load Result"
  }

  fun viewFullBreakdown(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পূর্ণাঙ্গ স্ক্রোল ও বিবরণ"
    AppLanguage.ENGLISH -> "Full Scroll & Details"
  }

  fun fullHistoryDetailTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "৫০টি সংখ্যার যোগফল ও বিস্তারিত স্ক্রোল"
    AppLanguage.ENGLISH -> "50 Numbers Sum & Full Scroll"
  }

  fun scrollTip(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "স্ক্রোল করে সব সংখ্যা দেখুন"
    AppLanguage.ENGLISH -> "Scroll to view all numbers"
  }

  fun addSample50Sum(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "৫০ সংখ্যার যোগফলের নমুনা"
    AppLanguage.ENGLISH -> "Sample 50 Numbers Sum"
  }

  fun itemizedNumbersTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ক্রমিক নম্বর অনুযায়ী ৫০টি সংখ্যার তালিকা:"
    AppLanguage.ENGLISH -> "Itemized list of 50 numbers:"
  }

  fun numbersCountBadge(count: Int, lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "$count টি সংখ্যার যোগফল"
    AppLanguage.ENGLISH -> "$count numbers sum"
  }

  fun wrapMode(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোড়ানো (↑↓ স্ক্রোল)"
    AppLanguage.ENGLISH -> "Wrapped (↑↓)"
  }

  fun singleLineMode(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এক লাইনে (←→ স্ক্রোল)"
    AppLanguage.ENGLISH -> "Single Line (←→)"
  }

  fun closeButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বন্ধ করুন"
    AppLanguage.ENGLISH -> "Close"
  }

  fun copyFormula(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব কপি"
    AppLanguage.ENGLISH -> "Copy Formula"
  }

  fun scientificMode(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "উন্নত / সায়েন্টিফিক"
    AppLanguage.ENGLISH -> "Scientific Mode"
  }

  // Age Calculator
  fun ageCalcTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বয়স ক্যালকুলেটর"
    AppLanguage.ENGLISH -> "Age Calculator"
  }

  fun ageCalcSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আপনার নির্ভুল বয়স ও জন্মদিন জানুন"
    AppLanguage.ENGLISH -> "Calculate Exact Age & Birthdays"
  }

  fun dobLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "জন্ম তারিখ (Date of Birth)"
    AppLanguage.ENGLISH -> "Date of Birth"
  }

  fun targetDateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "যে তারিখ পর্যন্ত বয়স (Target Date)"
    AppLanguage.ENGLISH -> "Age on Target Date"
  }

  fun calculateAgeButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বয়স বের করুন"
    AppLanguage.ENGLISH -> "Calculate Age"
  }

  fun exactAgeTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আপনার বর্তমান বয়স"
    AppLanguage.ENGLISH -> "Your Exact Age"
  }

  fun yearsLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বছর"
    AppLanguage.ENGLISH -> "Years"
  }

  fun monthsLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মাস"
    AppLanguage.ENGLISH -> "Months"
  }

  fun daysUnit(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দিন"
    AppLanguage.ENGLISH -> "Days"
  }

  fun nextBirthdayTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পরবর্তী জন্মদিন"
    AppLanguage.ENGLISH -> "Next Birthday"
  }

  fun lifeStatsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "জীবনের মোট পরিসংখ্যান"
    AppLanguage.ENGLISH -> "Total Life Statistics"
  }

  fun totalMonths(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট মাস"
    AppLanguage.ENGLISH -> "Total Months"
  }

  fun totalWeeks(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট সপ্তাহ"
    AppLanguage.ENGLISH -> "Total Weeks"
  }

  fun totalDays(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট দিন"
    AppLanguage.ENGLISH -> "Total Days"
  }

  fun totalHours(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট ঘণ্টা"
    AppLanguage.ENGLISH -> "Total Hours"
  }

  fun totalMinutes(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট মিনিট"
    AppLanguage.ENGLISH -> "Total Minutes"
  }

  fun upcomingBirthdaysTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আসন্ন ৫ বছরের জন্মদিন (বারসহ)"
    AppLanguage.ENGLISH -> "Next 5 Birthdays & Weekdays"
  }

  fun zodiacTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "রাশি ও ব্যক্তিত্ব (Zodiac)"
    AppLanguage.ENGLISH -> "Zodiac Sign & Symbol"
  }

  fun dateDiffTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দুই তারিখের ব্যবধান বের করুন"
    AppLanguage.ENGLISH -> "Date Difference Calculator"
  }

  fun historySixMonthsBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "৬ মাসের হিস্ট্রি সংরক্ষিত"
    AppLanguage.ENGLISH -> "6-Month History Retained"
  }

  fun historyRetentionSubtext(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "স্বয়ংক্রিয়ভাবে বিগত ৬ মাসের সকল হিসাব সংরক্ষিত থাকে"
    AppLanguage.ENGLISH -> "Automatically stores calculations for the past 6 months"
  }

  fun filterAll(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সব (৬ মাস)"
    AppLanguage.ENGLISH -> "All (6 Mo)"
  }

  fun filterToday(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আজ"
    AppLanguage.ENGLISH -> "Today"
  }

  fun filterThisWeek(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এই সপ্তাহ"
    AppLanguage.ENGLISH -> "This Week"
  }

  fun filterThisMonth(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এই মাস"
    AppLanguage.ENGLISH -> "This Month"
  }

  fun filterOlder(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পূর্ববর্তী মাসসমূহ"
    AppLanguage.ENGLISH -> "Older Months"
  }

  fun calculationDateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাবের তারিখ ও সময়"
    AppLanguage.ENGLISH -> "Calculation Date & Time"
  }

  fun loadSixMonthSamples(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "+ ৬ মাসের নমুনা"
    AppLanguage.ENGLISH -> "+ 6-Mo Sample"
  }

  // Loan & EMI Calculator
  fun tabLoanCalc(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "লোন ও EMI"
    AppLanguage.ENGLISH -> "Loan / EMI"
  }

  fun loanCalcTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "লোন ও EMI ক্যালকুলেটর"
    AppLanguage.ENGLISH -> "Loan & EMI Calculator"
  }

  fun loanCalcSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ব্যাংক লোন, ইএমআই ও কিস্তির নির্ভুল হিসাব"
    AppLanguage.ENGLISH -> "Calculate Monthly EMI, Total Interest & Schedule"
  }

  fun loanCategoryTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ঋণের ধরন (Presets)"
    AppLanguage.ENGLISH -> "Loan Type Presets"
  }

  fun homeLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "গৃহ ঋণ"
    AppLanguage.ENGLISH -> "Home Loan"
  }

  fun personalLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ব্যক্তিগত"
    AppLanguage.ENGLISH -> "Personal"
  }

  fun carLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "গাড়ি ঋণ"
    AppLanguage.ENGLISH -> "Car Loan"
  }

  fun educationLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শিক্ষা ঋণ"
    AppLanguage.ENGLISH -> "Education"
  }

  fun businessLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ব্যবসা ঋণ"
    AppLanguage.ENGLISH -> "Business"
  }

  fun customLoan(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "কাস্টম"
    AppLanguage.ENGLISH -> "Custom"
  }

  fun principalAmountLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ঋণের পরিমাণ (Principal Amount)"
    AppLanguage.ENGLISH -> "Loan Amount (Principal)"
  }

  fun interestRateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বার্ষিক সুদের হার (% p.a.)"
    AppLanguage.ENGLISH -> "Interest Rate (% per annum)"
  }

  fun tenureLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ঋণের মেয়াদ (Tenure)"
    AppLanguage.ENGLISH -> "Loan Tenure"
  }

  fun interestMethodLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সুদের হিসাবের ধরন"
    AppLanguage.ENGLISH -> "Interest Method"
  }

  fun reducingBalanceLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হ্রাসমান ব্যালান্স (Reducing EMI)"
    AppLanguage.ENGLISH -> "Reducing Balance (Standard EMI)"
  }

  fun flatRateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ফ্ল্যাট রেট (Flat Rate)"
    AppLanguage.ENGLISH -> "Flat Rate"
  }

  fun monthlyEmiTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রতি মাসের কিস্তি (Monthly EMI)"
    AppLanguage.ENGLISH -> "Monthly EMI"
  }

  fun totalInterestTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট প্রদেয় সুদ"
    AppLanguage.ENGLISH -> "Total Interest Payable"
  }

  fun totalPaymentTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সুদসহ মোট পরিশোধ"
    AppLanguage.ENGLISH -> "Total Payment (Principal + Interest)"
  }

  fun breakdownChartTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মূল ঋণ ও সুদের অনুপাত"
    AppLanguage.ENGLISH -> "Principal vs. Interest Breakdown"
  }

  fun principalShareLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মূল ঋণ"
    AppLanguage.ENGLISH -> "Principal"
  }

  fun interestShareLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট সুদ"
    AppLanguage.ENGLISH -> "Total Interest"
  }

  fun principalCardTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মূল ঋণ (Principal)"
    AppLanguage.ENGLISH -> "Principal Loan Amount"
  }

  fun totalInterestCardTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট প্রদেয় সুদ (Total Interest)"
    AppLanguage.ENGLISH -> "Total Interest Payable"
  }

  fun totalPaymentCardTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সর্বমোট প্রদেয় (Total Payment)"
    AppLanguage.ENGLISH -> "Total Payment (Principal + Interest)"
  }

  fun paymentFormulaBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মূল ঋণ + মোট সুদ = সর্বমোট পরিশোধ"
    AppLanguage.ENGLISH -> "Principal + Total Interest = Total Payment"
  }

  fun itemizedBreakdownTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পরিশোধের স্পষ্ট হিসাব (Summary Breakdown)"
    AppLanguage.ENGLISH -> "Complete Payment Breakdown"
  }

  fun exactAmountLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "দশমিকসহ নিখুঁত মান:"
    AppLanguage.ENGLISH -> "Exact amount:"
  }

  fun interestOfPrincipalLabel(pct: String, lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আসলের $pct% সুদ"
    AppLanguage.ENGLISH -> "$pct% of loan amount"
  }

  fun totalInstallmentsLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মোট কিস্তির সংখ্যা"
    AppLanguage.ENGLISH -> "Total Installments"
  }

  fun installmentsCount(count: Int, lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "$count টি কিস্তি"
    AppLanguage.ENGLISH -> "$count Installments"
  }

  fun perMonthSuffix(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "/ মাস"
    AppLanguage.ENGLISH -> "/ month"
  }

  fun scheduleTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পরিশোধের সময়সূচি (Amortization Schedule)"
    AppLanguage.ENGLISH -> "Repayment Schedule (Amortization)"
  }

  fun yearlyViewLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বছরভিত্তিক (Yearly)"
    AppLanguage.ENGLISH -> "Yearly Summary"
  }

  fun monthlyViewLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মাসিক (Monthly)"
    AppLanguage.ENGLISH -> "Monthly Breakdown"
  }

  fun yearHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বছর"
    AppLanguage.ENGLISH -> "Year"
  }

  fun monthHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মাস"
    AppLanguage.ENGLISH -> "Month"
  }

  fun openingHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "শুরুর ব্যালান্স"
    AppLanguage.ENGLISH -> "Opening"
  }

  fun principalHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মূল পরিশোধ"
    AppLanguage.ENGLISH -> "Principal"
  }

  fun interestHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সুদ পরিশোধ"
    AppLanguage.ENGLISH -> "Interest"
  }

  fun balanceHeaderLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "অবশিষ্ট ঋণ"
    AppLanguage.ENGLISH -> "Balance"
  }

  fun extraMonthlyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "অতিরিক্ত মাসিক কিস্তির সুবিধা (ঐচ্ছিক)"
    AppLanguage.ENGLISH -> "Extra Monthly Prepayment Benefit"
  }

  fun extraMonthlyHelper(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "প্রতি মাসে কিছু বাড়তি টাকা দিলে ঋণ কত দ্রুত শেষ হবে এবং কত সুদ বাঁচবে দেখুন"
    AppLanguage.ENGLISH -> "See how much tenure & interest you save by adding an extra amount each month"
  }

  fun tenureSavedLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "মেয়াদ হ্রাস পাবে"
    AppLanguage.ENGLISH -> "Tenure Shortened"
  }

  fun interestSavedLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সুদ সাশ্রয় হবে"
    AppLanguage.ENGLISH -> "Interest Saved"
  }

  fun copyLoanReport(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "লোনের সারাংশ কপি"
    AppLanguage.ENGLISH -> "Copy Loan Summary"
  }

  fun shareLoanReport(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "লোনের রিপোর্ট শেয়ার"
    AppLanguage.ENGLISH -> "Share Loan Report"
  }

  // Chained Number Editing & Full Screen History
  fun fullScreenHistoryTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ক্যালকুলেশন হিস্টোরি"
    AppLanguage.ENGLISH -> "Calculation History"
  }

  fun fullScreenHistorySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বিগত ৬ মাসের সংরক্ষিত হিসাব ও পূর্ণাঙ্গ স্ক্রোল"
    AppLanguage.ENGLISH -> "6-Month Archive with Full Scrolling"
  }

  fun searchHistoryPlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব বা সংখ্যা খুঁজুন (যেমন: ১৫০, ৬৪০)..."
    AppLanguage.ENGLISH -> "Search calculations or numbers..."
  }

  fun chainedNumbersTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "পরপর সংখ্যার তালিকা (ট্যাপ করে পরিবর্তন করুন)"
    AppLanguage.ENGLISH -> "Chained Numbers (Tap to Edit)"
  }

  fun editNumberTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সংখ্যা এডিট করুন"
    AppLanguage.ENGLISH -> "Edit Number"
  }

  fun editNumberSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সমীকরণের নির্দিষ্ট সংখ্যার মান পরিবর্তন করুন"
    AppLanguage.ENGLISH -> "Change the value of this number in the equation"
  }

  fun originalNumber(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "বর্তমান মান"
    AppLanguage.ENGLISH -> "Current Value"
  }

  fun newNumber(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "নতুন মান লিখুন"
    AppLanguage.ENGLISH -> "Enter New Value"
  }

  fun updateNumberButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "আপডেট করুন"
    AppLanguage.ENGLISH -> "Update Number"
  }

  fun tapToEditTip(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "যেকোনো সংখ্যা বা অঙ্কে ট্যাপ করে সরাসরি এডিট করতে পারবেন"
    AppLanguage.ENGLISH -> "Tap any number or digit to edit directly"
  }

  fun cursorControl(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "কার্সর"
    AppLanguage.ENGLISH -> "Cursor"
  }

  fun clearAllHistoryConfirmTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "সকল হিস্ট্রি মুছে ফেলতে চান?"
    AppLanguage.ENGLISH -> "Clear All History?"
  }

  fun clearAllHistoryConfirmMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "৬ মাসের সকল সংরক্ষিত ক্যালকুলেশন স্থায়ীভাবে মুছে যাবে।"
    AppLanguage.ENGLISH -> "All stored calculations from the past 6 months will be permanently deleted."
  }

  fun confirmClear(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হ্যাঁ, মুছে ফেলুন"
    AppLanguage.ENGLISH -> "Yes, Clear All"
  }

  fun loadInEditorButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "এডিটরে নিন ও এডিট করুন"
    AppLanguage.ENGLISH -> "Load & Edit in Calculator"
  }

  fun useResultButton(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ফলাফল ব্যবহার করুন"
    AppLanguage.ENGLISH -> "Use Result"
  }

  fun copyCalculation(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "হিসাব কপি"
    AppLanguage.ENGLISH -> "Copy"
  }

  fun backToCalculator(lang: AppLanguage) = when (lang) {
    AppLanguage.BENGALI -> "ক্যালকুলেটরে ফিরুন"
    AppLanguage.ENGLISH -> "Back to Calculator"
  }
}

