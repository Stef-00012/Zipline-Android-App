import Button from "@/components/Button";
import ChartLegend from "@/components/ChartLegend";
import DatePicker from "@/components/DatePicker";
import MaterialSymbols from "@/components/MaterialSymbols";
import Skeleton from "@/components/skeleton/Skeleton";
import SkeletonTable from "@/components/skeleton/Table";
import Table from "@/components/Table";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import {
	colorHash,
	convertToBytes,
	getMetricsDifference,
} from "@/functions/util";
import {
	filterStats,
	getStats,
	type StatsProps,
} from "@/functions/zipline/stats";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/metrics";
import type { APIStats } from "@/types/zipline";
import { add } from "date-fns";
import { useContext, useEffect, useState } from "react";
import { Dimensions, ScrollView, Text, View } from "react-native";
import { LineChart, PieChart } from "react-native-gifted-charts";
import type { DateType } from "react-native-ui-datepicker";

export default function Metrics() {
	const { webSettings } = useContext(ZiplineContext);

	const adminOnly = webSettings
		? webSettings.config.features.metrics.adminOnly
		: true;

	useAuth(adminOnly ? "ADMIN" : "USER");
	useShareIntent();

	const [stats, setStats] = useState<APIStats | null>();
	const [userSpecificMetrics, setUserSpecificMetrics] =
		useState<boolean>(false);
	const [filteredStats, setFilteredStats] = useState<APIStats | null>(null);
	const [mainStat, setMainStat] = useState<APIStats[0] | null>(null);
	const [statsDifferences, setStatsDifferences] = useState<{
		files: number;
		urls: number;
		storage: number;
		users: number;
		fileViews: number;
		urlViews: number;
	}>({
		files: 0,
		fileViews: 0,
		storage: 0,
		urls: 0,
		urlViews: 0,
		users: 0,
	});

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

	// biome-ignore lint/correctness/useExhaustiveDependencies: Functions should not be parameters of the effect
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

		setUserSpecificMetrics(
			webSettings
				? webSettings.config.features.metrics.showUserSpecific
				: false,
		);

		if (typeof stats === "string") return setStats(null);

		const sortedStats = stats.sort((a, b) => {
			return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
		});

		setStats(sortedStats);

		const firstStat = sortedStats[sortedStats.length - 1].data;
		const lastStat = sortedStats[0].data;

		const statsDiff = {
			files: getMetricsDifference(firstStat.files, lastStat.files),
			fileViews: getMetricsDifference(firstStat.fileViews, lastStat.fileViews),
			storage: getMetricsDifference(firstStat.storage, lastStat.storage),
			urls: getMetricsDifference(firstStat.urls, lastStat.urls),
			urlViews: getMetricsDifference(firstStat.urlViews, lastStat.urlViews),
			users: getMetricsDifference(firstStat.users, lastStat.users),
		};

		setStatsDifferences(statsDiff);
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
						containerStyle={{
							marginBottom: 10,
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
				</View>

				<Button
					onPress={() => {
						setDatePickerOpen(true);
					}}
					color="transparent"
					text="Change Date Range"
					containerStyle={{
						borderWidth: 2,
						borderColor: "#222c47",
						marginHorizontal: 10,
						marginBottom: 10
					}}
					rippleColor="#283557"
					disabled={!filteredStats || !mainStat}
					textColor={filteredStats && mainStat ? "white" : "gray"}
				/>

				{filteredStats && mainStat ? (
					<View>
						<ScrollView style={{ height: "93%" }}>
							<ScrollView horizontal style={styles.scrollView}>
								{[
									{
										title: "Files:",
										amount: mainStat.data.files,
										difference: statsDifferences.files,
									},
									{
										title: "URLs:",
										amount: mainStat.data.urls,
										difference: statsDifferences.urls,
									},
									{
										title: "Storage Used:",
										amount: convertToBytes(mainStat.data.storage, {
											unitSeparator: " ",
										}),
										difference: statsDifferences.storage,
									},
									{
										title: "Users:",
										amount: mainStat.data.users,
										difference: statsDifferences.users,
									},
									{
										title: "File Views:",
										amount: mainStat.data.fileViews,
										difference: statsDifferences.fileViews,
									},
									{
										title: "URL Views:",
										amount: mainStat.data.urlViews,
										difference: statsDifferences.urlViews,
									},
								].map((stat) => (
									<View key={stat.title} style={styles.statContainer}>
										<Text style={styles.subHeaderText}>{stat.title}</Text>

										<View style={styles.statContainerData}>
											<Text style={styles.statText}>{stat.amount}</Text>

											<View
												style={{
													...styles.statDifferenceContainer,
													backgroundColor:
														stat.difference === 0
															? "#868E9640"
															: stat.difference > 0
																? "#40C05740"
																: "#FA525240",
												}}
											>
												{stat.difference > 0 ? (
													<MaterialSymbols
														name="north"
														size={18}
														color="#69db7c"
													/>
												) : stat.difference < 0 ? (
													<MaterialSymbols
														name="south"
														size={18}
														color="#ff8787"
													/>
												) : (
													<MaterialSymbols
														name="remove"
														size={18}
														color="#ced4da"
													/>
												)}

												<Text
													style={{
														...styles.statDifferenceText,
														color:
															stat.difference === 0
																? "#ced4da"
																: stat.difference > 0
																	? "#69db7c"
																	: "#ff8787",
													}}
												>
													{stat.difference}%
												</Text>
											</View>
										</View>
									</View>
								))}
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
											headerRow={[
												{
													row: "User",
												},
												{
													row: "URLs",
												},
												{
													row: "Views",
												},
											]}
											rowWidth={[190, 100, 100]}
											rows={mainStat.data.urlsUsers.map((userUrl) => {
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
											headerRow={[
												{
													row: "User",
												},
												{
													row: "Files",
												},
												{
													row: "Storage Used",
												},
												{
													row: "Views",
												},
											]}
											rowWidth={[150, 60, 130, 50]}
											rows={mainStat.data.filesUsers.map((userFile) => {
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
											headerRow={[
												{
													row: "Type",
												},
												{
													row: "Files",
												},
											]}
											rowWidth={[tableTypeWidth, tableFilesWidth]}
											rows={mainStat.data.types.map((typeData) => {
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
					<View style={styles.mainContainer}>
						<Skeleton.Group show={!filteredStats || !mainStat}>
							<ScrollView style={{ height: "93%" }}>
								<ScrollView horizontal style={styles.scrollView}>
									{[
										"Files:",
										"URLs:",
										"Storage Used:",
										"Users:",
										"File Views:",
										"URL Views:",
									].map((stat) => (
										<View key={stat} style={styles.statContainer}>
											<Text style={styles.subHeaderText}>{stat}</Text>

											<View style={styles.statContainerData}>
												<Skeleton height={36} width={60} />
												<View
													style={{
														width: 5,
													}}
												/>
												<View
													style={{
														marginTop: 9,
													}}
												>
													<Skeleton height={27} width={40} />
												</View>
											</View>
										</View>
									))}
								</ScrollView>

								{userSpecificMetrics && (
									<>
										<View
											style={{
												...styles.chartContainer,
												padding: 0,
											}}
										>
											<SkeletonTable
												headerRow={["User", "URLs", "Views"]}
												rowWidth={[190, 100, 100]}
												rows={[[80, 50]]}
											/>
										</View>

										<View
											style={{
												...styles.chartContainer,
												padding: 0,
											}}
										>
											<SkeletonTable
												headerRow={["User", "Files", "Storage Used", "Views"]}
												rowWidth={[150, 60, 130, 50]}
												rows={[[70, 50, 100, 50]]}
											/>
										</View>

										<View
											style={{
												...styles.chartContainer,
												padding: 0,
											}}
										>
											<SkeletonTable
												headerRow={["Type", "Files"]}
												rowWidth={[tableTypeWidth, tableFilesWidth]}
												rows={[...Array(4).keys()].map(() => {
													return ["55%", 30];
												})}
											/>
										</View>
									</>
								)}

								<View style={styles.chartContainer}>
									<View style={styles.pieChartContainer}>
										<Skeleton radius="round" width={250} height={250} />
									</View>

									<View
										style={{
											flexDirection: "row",
											marginTop: 10,
										}}
									>
										{[...Array(5).keys()].map((index) => (
											<View
												key={index}
												style={{
													marginHorizontal: 2.5,
												}}
											>
												<Skeleton width={60} height={16} />
											</View>
										))}
									</View>
								</View>

								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Count</Text>

									<Skeleton width="100%" height={220} />

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

								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Views</Text>

									<Skeleton width="100%" height={220} />

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

								<View style={styles.chartContainer}>
									<Text style={styles.chartTitle}>Storage Used</Text>

									<Skeleton width="100%" height={220} />

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
						</Skeleton.Group>
					</View>
				)}
			</View>
		</View>
	);
}
