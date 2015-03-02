<?php
	$first_half = "http://query.yahooapis.com/v1/public/yql?q=Select%20Name%2C%20Symbol%2C%20LastTradePriceOnly%2C%20Change%2C%20ChangeinPercent%2C%20PreviousClose%2C%20DaysLow%2C%20DaysHigh%2C%20Open%2C%20YearLow%2C%20YearHigh%2C%20Bid%2C%20Ask%2C%20AverageDailyVolume%2C%20OneyrTargetPrice%2C%20MarketCapitalization%2C%20Volume%2C%20Open%2C%20YearLow%20from%20yahoo.finance.quotes%20where%20symbol%3D%22";
	$second_half = "%22&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	$third_half = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=";
	$fourth_half = "&region=US&lang=en-US";
	$fifth_half = "http://chart.finance.yahoo.com/t?s=";
	$sixth_half = "&amp;lang=en-US&amp;amp;width=300&amp;height=180";
	$name = $namerr = "";
	$p_result = "";
	
	$symbol = $_GET["symbol"];
	//echo $symbol;

	$full = $first_half.$symbol.$second_half;
	$sum = $third_half.$symbol.$fourth_half;
	$StockChartImage = $fifth_half.$symbol.$sixth_half;

	
	if(@simplexml_load_file($full)){

		$xml = simplexml_load_file($full);//stock info
		$news = simplexml_load_file($sum);//news feed
		//store parsed data

		if($xml->results->quote->Bid == ""){
			$Bid = " ";
		}else{
		 	$Bid = number_format((float)$xml->results->quote->Bid,2); 
		}
		if($xml->results->quote->Change == ""){
		 	$Change = " ";
		}else{
		 	$Change = number_format((float)$xml->results->quote->Change,2); 
		}
		if($xml->results->quote->DaysLow == ""){
			$DaysLow = " ";
		}else{
			$DaysLow = number_format((float)$xml->results->quote->DaysLow,2);
		}
		if($xml->results->quote->DaysHigh == ""){
			$DaysHigh = " ";
		}else{
			$DaysHigh = number_format((float)$xml->results->quote->DaysHigh,2); 
		}
		if($xml->results->quote->YearLow == ""){
			$YearLow = " ";
		}else{
			$YearLow = number_format((float)$xml->results->quote->YearLow,2);
		}
		if($xml->results->quote->YearHigh == ""){
			$YearHigh = " ";
		}else{
			$YearHigh = number_format((float)$xml->results->quote->YearHigh,2);
		}

		if($xml->results->quote->MarketCapitalization == ""){
			$MarketCapitalization = " ";
		}else{
			$MarketCapitalization = number_format((float)$xml->results->quote->MarketCapitalization,2);
		}
		 
		$lastchr = substr($xml->results->quote->MarketCapitalization, -1);

		if($xml->results->quote->LastTradePriceOnly == ""){
			$LastTradePriceOnly = " ";
		}else{
			$LastTradePriceOnly = number_format((float)$xml->results->quote->LastTradePriceOnly,2);
		}
		$Name = $xml->results->quote->Name->asXML(); 
		if($xml->results->quote->Open == ""){
			$Open = " ";
		}else{
			$Open = number_format((float)$xml->results->quote->Open,2);
		}
		if($xml->results->quote->PreviousClose == ""){
			$PreviousClose = " ";
		}else{
			$PreviousClose = number_format((float)$xml->results->quote->PreviousClose,2);
		}
		if($xml->results->quote->ChangeinPercent == ""){
			$ChangeinPercent = " ";
		}else{
			$ChangeinPercent = number_format((float)$xml->results->quote->ChangeinPercent,2); 
		}
		
		$Symbol = $xml->results->quote->Symbol; 
		if($xml->results->quote->Ask == ""){
			$Ask = " ";
		}else{
			$Ask = number_format((float)$xml->results->quote->Ask,2);
		}
		if($xml->results->quote->OneyrTargetPrice == ""){
			$OneyrTargetPrice = " ";
		}else{
			$OneyrTargetPrice = number_format((float)$xml->results->quote->OneyrTargetPrice,2);	
		}
		if($xml->results->quote->Volume == ""){
			$Volume = " ";
		}else{
			$Volume = number_format((float)$xml->results->quote->Volume,0); 
		}
		if($xml->results->quote->AverageDailyVolume == ""){
			$AverageDailyVolume = " ";
		}else{
			$AverageDailyVolume = number_format((float)$xml->results->quote->AverageDailyVolume,0);	
		}
		
		$Percent = number_format((float)$Change*100/(float)$LastTradePriceOnly,2);


		if($xml->results->quote->Change == ""){
			$xmlerr = "Stock Information Not Available";
			$p_result .= "<results>";
			$p_result .= "<error>".$xmlerr."</error>";
			$p_result .= "</results>";
			echo $p_result;
		}else{

			$p_result .= "<results>";
			$p_result .= $Name;
			$p_result .= "<Symbol>".$Symbol."</Symbol>";
			$p_result .= "<qoute>";
			$p_result .= "<Bid>".$Bid."</Bid>";
			$p_result .= "<Change>".$Change."</Change>";
			$p_result .= "<DaysLow>".$DaysLow."</DaysLow>";
			$p_result .= "<DaysHigh>".$DaysHigh."</DaysHigh>";
			$p_result .= "<YearLow>".$YearLow."</YearLow>";
			$p_result .= "<YearHigh>".$YearHigh."</YearHigh>";
			$p_result .= "<MarketCapitalization>".$MarketCapitalization."</MarketCapitalization>";
			$p_result .= "<LastTradePriceOnly>".$LastTradePriceOnly."</LastTradePriceOnly>";
			$p_result .= "<Open>".$Open."</Open>";
			$p_result .= "<PreviousClose>".$PreviousClose."</PreviousClose>";
			$p_result .= "<ChangeinPercent>".$ChangeinPercent."</ChangeinPercent>";
			$p_result .= "<OneyrTargetPrice>".$OneyrTargetPrice."</OneyrTargetPrice>";
			$p_result .= "<Volume>".$Volume."</Volume>";
			$p_result .= "<Ask>".$Ask."</Ask>";
			$p_result .= "<AverageDailyVolume>".$AverageDailyVolume."</AverageDailyVolume>";
			$p_result .= "</qoute>";
			//$p_result .= "</results>";
			if($news->channel->item->children() == "Yahoo! Finance: RSS feed not found"){
			    $p_result .= "<StockChartImageURL>".$StockChartImage."</StockChartImageURL>";
			    $p_result .= "</results>";
				echo $p_result;
			}else{
				$p_result .= "<News>";
			
				foreach($news->channel->item as $item){
					$p_result .= "<Item>";
					$title = htmlentities($item->title,ENT_QUOTES,'UTF-8');
					//$title = $item->title->asXML();
					$lind = $item->link->asXML();
					$p_result .= "<title>".$title."</title>";
					$p_result .= $lind;
					$p_result .= "</Item>";
				}
				$p_result .= "</News>";
				$p_result .= "<StockChartImageURL>".$StockChartImage."</StockChartImageURL>";
				$p_result .= "</results>";
				echo $p_result;
			}

		}
	}else{
		$xmlerr = "Stock Information Not Available";
		$p_result .= "<results>";
		$p_result .= "<error>".$xmlerr."</error>";
		$p_result .= "</results>";
		echo $p_result;		
	}

	

	
	
?> 
	


