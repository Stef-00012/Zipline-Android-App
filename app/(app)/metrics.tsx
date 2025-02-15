import { Dimensions, ScrollView, Text, View } from "react-native";
import { LineChart, PieChart } from "react-native-gifted-charts";
import { colorHash, convertToBytes } from "@/functions/util";
import type { DateType } from "react-native-ui-datepicker";
import { getSettings } from "@/functions/zipline/settings";
import { useShareIntent } from "@/hooks/useShareIntent";
import ChartLegend from "@/components/ChartLegend";
import DatePicker from "@/components/DatePicker";
import type { APIStats } from "@/types/zipline";
import { useEffect, useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { styles } from "@/styles/metrics";
import Button from "@/components/Button";
import { add } from "date-fns";
import {
	filterStats,
	getStats,
	type StatsProps,
} from "@/functions/zipline/stats";
import React from "react";
import Table from "@/components/Table";

export default function Metrics() {
	useAuth();
	useShareIntent();

	const [stats, setStats] = useState<APIStats | null>();
	const [userSpecificMetrics, setUserSpecificMetrics] =
		useState<boolean>(false);
	const [filteredStats, setFilteredStats] = useState<APIStats | null>(null);
	const [mainStat, setMainStat] = useState<APIStats[0] | null>(null);

	const [datePickerOpen, setDatePickerOpen] = useState(false);
	const [allTime, setAllTime] = useState<boolean>(false);

	const [range, setRange] = useState<{
		startDate: DateType;
		endDate: DateType;
	}>({
		startDate: add(new Date(), {
			weeks: -1,
		}),
		endDate: new Date(),
	});

	useEffect(() => {
		updateStats({
			from: range.startDate as string,
			to: range.endDate as string,
			all: allTime,
		});
	}, [range, allTime]);

	useEffect(() => {
		if (stats) {
			const filteredStats = filterStats(stats);

			setMainStat(filteredStats[filteredStats.length - 1]);

			setFilteredStats(filteredStats);
		}
	}, [stats]);

	async function updateStats({ from, to, all }: StatsProps) {
		if (!all && (!from || !to)) return;

		setStats(null);
		setFilteredStats(null);

		const stats = await getStats({
			from,
			to,
			all,
		});
		const settings = await getSettings();

		setUserSpecificMetrics(
			typeof settings === "string"
				? false
				: settings.featuresMetricsShowUserSpecific,
		);

		if (typeof stats === "string") return setStats(null);

		const sortedStats = stats.sort((a, b) => {
			return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
		});

		setStats(sortedStats);
	}

	const windowWidth = Dimensions.get("window").width;

	const chartWidth = windowWidth - 20 * 2 - 45;

	const tableTypeWidth =
		((windowWidth - styles.chartContainer.margin * 2) / 3) * 2;
	const tableFilesWidth = (windowWidth - styles.chartContainer.margin * 2) / 3;

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<DatePicker
					open={datePickerOpen}
					onClose={() => {
						setDatePickerOpen(false);

						if (!range.endDate && range.startDate) {
							setRange({
								startDate: add(range.startDate as string, {
									weeks: -1,
								}),
								endDate: range.startDate,
							});
						}
					}}
					onChange={(params) => {
						setAllTime(false);

						setRange(params);
					}}
					mode="range"
					startDate={range.startDate}
					endDate={range.endDate}
					maxDate={new Date()}
				>
					<Button
						text="Show All Time"
						color="#171c39"
						margin={{
							bottom: 10,
						}}
						onPress={() => {
							setDatePickerOpen(false);
							setAllTime(true);

							if (!range.endDate && range.startDate) {
								setRange({
									startDate: add(range.startDate as string, {
										weeks: -1,
									}),
									endDate: range.startDate,
								});
							}
						}}
					/>
				</DatePicker>

				<View style={styles.header}>
					<Text style={styles.headerText}>Metrics</Text>

					<Text style={styles.dateRangeText}>
						{allTime
							? "All Time"
							: `${new Date(range.startDate as string).toLocaleDateString()}${
									range.endDate
										? ` to ${new Date(range.endDate as string).toLocaleDateString()}`
										: ""
								}`}
					</Text>

					<Button
						onPress={() => {
							setDatePickerOpen(true);
						}}
						color="transparent"
						text="Change Date Range"
						borderWidth={2}
						borderColor="#222c47"
						margin={{
							right: 10,
							top: 10,
						}}
						rippleColor="#283557"
						disabled={!filteredStats || !mainStat}
						textColor={filteredStats && mainStat ? "white" : "gray"}
					/>
				</View>

				{filteredStats && mainStat ? (
					<View>
						<ScrollView>
							<ScrollView
								horizontal
								style={{
									...styles.scrollView,
									...styles.statsContainer,
								}}
							>
								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>Files:</Text>
									<Text style={styles.statText}>{mainStat.data.files}</Text>
								</View>

								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>URLs:</Text>
									<Text style={styles.statText}>{mainStat.data.urls}</Text>
								</View>

								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>Storage Used:</Text>
									<Text style={styles.statText}>
										{convertToBytes(mainStat.data.storage, {
											unitSeparator: " ",
										})}
									</Text>
								</View>

								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>Users:</Text>
									<Text style={styles.statText}>{mainStat.data.users}</Text>
								</View>

								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>File Views:</Text>
									<Text style={styles.statText}>{mainStat.data.fileViews}</Text>
								</View>

								<View style={styles.statContainer}>
									<Text style={styles.subHeaderText}>URL Views:</Text>
									<Text style={styles.statText}>{mainStat.data.urlViews}</Text>
								</View>
							</ScrollView>

							{userSpecificMetrics && (
								<>
									<View
										style={{
											...styles.chartContainer,
											padding: 0,
										}}
									>
										<Table
											headerRow={["User", "URLs", "Views"]}
											rowWidth={[180, 100, 100]}
											rows={mainStat.data.urlsUsers.map((userUrl, index) => {
												const username = (
													<Text key={userUrl.username} style={styles.rowText}>
														{userUrl.username}
													</Text>
												);

												const urls = (
													<Text key={userUrl.username} style={styles.rowText}>
														{userUrl.sum}
													</Text>
												);

												const views = (
													<Text key={userUrl.username} style={styles.rowText}>
														{userUrl.views}
													</Text>
												);

												let rowStyle = styles.row;

												if (index === 0)
													rowStyle = {
														...styles.row,
														...styles.firstRow,
													};

												if (index === mainStat.data.types.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return [username, urls, views];
											})}
										/>
									</View>

									<View
										style={{
											...styles.chartContainer,
											padding: 0,
										}}
									>
										<Table
											headerRow={["User", "Files", "Storage Used", "Views"]}
											rowWidth={[130, 50, 130, 50]}
											rows={mainStat.data.filesUsers.map((userFile, index) => {
												const username = (
													<Text key={userFile.username} style={styles.rowText}>
														{userFile.username}
													</Text>
												);

												const files = (
													<Text key={userFile.username} style={styles.rowText}>
														{userFile.sum}
													</Text>
												);

												const storageUsed = (
													<Text key={userFile.username} style={styles.rowText}>
														{convertToBytes(userFile.storage, {
															unitSeparator: " ",
														})}
													</Text>
												);

												const views = (
													<Text key={userFile.username} style={styles.rowText}>
														{userFile.views}
													</Text>
												);

												let rowStyle = styles.row;

												if (index === 0)
													rowStyle = {
														...styles.row,
														...styles.firstRow,
													};

												if (index === mainStat.data.types.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return [username, files, storageUsed, views];
											})}
										/>
									</View>

									<View
										style={{
											...styles.chartContainer,
											padding: 0,
										}}
									>
										<Table
											headerRow={["Type", "Files"]}
											rowWidth={[tableTypeWidth, tableFilesWidth]}
											rows={mainStat.data.types.map((typeData, index) => {
												const type = (
													<Text key={typeData.type} style={styles.rowText}>
														{typeData.type}
													</Text>
												);

												const files = (
													<Text key={typeData.type} style={styles.rowText}>
														{typeData.sum}
													</Text>
												);

												let rowStyle = styles.row;

												if (index === 0)
													rowStyle = {
														...styles.row,
														...styles.firstRow,
													};

												if (index === mainStat.data.types.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return [type, files];
											})}
										/>
									</View>
								</>
							)}

							{/* Count */}
							<View style={styles.chartContainer}>
								<View style={styles.pieChartContainer}>
									<PieChart
										data={mainStat.data.types.map((type) => ({
											value: type.sum,
											color: colorHash(type.type),
										}))}
									/>
								</View>

								<ChartLegend
									data={mainStat.data.types.map((type) => ({
										label: type.type,
										color: colorHash(type.type),
									}))}
								/>
							</View>

							{/* Count */}
							<View style={styles.chartContainer}>
								<Text style={styles.chartTitle}>Count</Text>

								<LineChart
									width={chartWidth}
									xAxisLength={chartWidth}
									rulesLength={chartWidth}
									spacing={90}
									areaChart
									hideDataPoints
									rulesColor="gray"
									xAxisColor="gray"
									yAxisColor="transparent"
									xAxisLabelTextStyle={styles.chartXAxisLabelText}
									yAxisTextStyle={styles.chartYAxisTextStyle}
									xAxisTextNumberOfLines={2}
									data={filteredStats.map((stat) => ({
										label: new Date(stat.createdAt).toLocaleString(),
										value: stat.data.files,
									}))}
									color1="#323ea8"
									startFillColor1="#323ea8"
									startOpacity1={0.8}
									endFillColor1="#0c101c"
									endOpacity1={0.2}
									data2={filteredStats.map((stat) => ({
										label: new Date(stat.createdAt).toLocaleString(),
										value: stat.data.urls,
									}))}
									color2="#2f9e44"
									startFillColor2="#2f9e44"
									startOpacity2={0.8}
									endFillColor2="#0c101c"
									endOpacity2={0.2}
								/>

								<ChartLegend
									data={[
										{
											label: "Files",
											color: "#323ea8",
										},
										{
											label: "URLs",
											color: "#2f9e44",
										},
									]}
								/>
							</View>

							{/* Views */}
							<View style={styles.chartContainer}>
								<Text style={styles.chartTitle}>Views</Text>

								<LineChart
									width={chartWidth}
									xAxisLength={chartWidth}
									rulesLength={chartWidth}
									spacing={90}
									areaChart
									hideDataPoints
									rulesColor="gray"
									xAxisColor="gray"
									yAxisColor="transparent"
									xAxisLabelTextStyle={styles.chartXAxisLabelText}
									yAxisTextStyle={styles.chartYAxisTextStyle}
									xAxisTextNumberOfLines={2}
									data={filteredStats.map((stat) => ({
										label: new Date(stat.createdAt).toLocaleString(),
										value: stat.data.fileViews,
									}))}
									color1="#323ea8"
									startFillColor1="#323ea8"
									startOpacity1={0.8}
									endFillColor1="#0c101c"
									endOpacity1={0.2}
									data2={filteredStats.map((stat) => ({
										label: new Date(stat.createdAt).toLocaleString(),
										value: stat.data.urlViews,
									}))}
									color2="#2f9e44"
									startFillColor2="#2f9e44"
									startOpacity2={0.8}
									endFillColor2="#0c101c"
									endOpacity2={0.2}
								/>

								<ChartLegend
									data={[
										{
											label: "File Views",
											color: "#323ea8",
										},
										{
											label: "URL Views",
											color: "#2f9e44",
										},
									]}
								/>
							</View>

							{/* Storage Used */}
							<View style={styles.chartContainer}>
								<Text style={styles.chartTitle}>Storage Used</Text>

								<LineChart
									areaChart
									startFillColor="#323ea8"
									startOpacity={0.8}
									endFillColor="#0c101c"
									endOpacity={0.2}
									width={chartWidth - 40}
									xAxisLength={chartWidth - 40}
									rulesLength={chartWidth - 40}
									spacing={90}
									hideDataPoints
									xAxisColor="gray"
									yAxisColor="transparent"
									xAxisLabelTextStyle={styles.chartXAxisLabelText}
									yAxisTextStyle={styles.chartYAxisTextStyle}
									xAxisTextNumberOfLines={2}
									rulesColor="gray"
									formatYLabel={(value) =>
										convertToBytes(value, {
											unitSeparator: " ",
										}) || "???"
									}
									yAxisLabelWidth={80}
									yAxisLabelContainerStyle={{
										justifyContent: "flex-end",
										paddingRight: 10,
									}}
									data={filteredStats.map((stat) => ({
										label: new Date(stat.createdAt).toLocaleString(),
										value: stat.data.storage,
										yAxisLabelText: convertToBytes(stat.data.storage, {
											unitSeparator: " ",
										}),
									}))}
									color="#323ea8"
								/>

								<ChartLegend
									data={[
										{
											label: "Storage Used",
											color: "#323ea8",
										},
									]}
								/>
							</View>
						</ScrollView>
					</View>
				) : (
					<View style={styles.loadingContainer}>
						<Text style={styles.loadingText}>Loading...</Text>
					</View>
				)}
			</View>
		</View>
	);
}
