import type { SelectProps } from "@/components/Select";

export const millisecondsMinute = 60 * 1000;
export const millisecondsHour = 60 * millisecondsMinute;
export const millisecondsDay = 24 * millisecondsHour;
export const millisecondsWeek = 7 * millisecondsDay;
export const millisecondsMonth = 30 * millisecondsDay;
export const millisecondsYear = 365 * millisecondsDay;

export const dates: SelectProps["data"] = [
	{
		label: "Never",
		value: "never",
		milliseconds: null,
	},
	{
		label: "5 minutes",
		value: "5m",
		milliseconds: 5 * millisecondsMinute,
	},
	{
		label: "10 minutes",
		value: "10m",
		milliseconds: 10 * millisecondsMinute,
	},
	{
		label: "15 minutes",
		value: "15m",
		milliseconds: 15 * millisecondsMinute,
	},
	{
		label: "30 minutes",
		value: "30m",
		milliseconds: 30 * millisecondsMinute,
	},
	{
		label: "1 hour",
		value: "1h",
		milliseconds: millisecondsHour,
	},
	{
		label: "2 hours",
		value: "2h",
		milliseconds: 2 * millisecondsHour,
	},
	{
		label: "3 hours",
		value: "3h",
		milliseconds: 3 * millisecondsHour,
	},
	{
		label: "4 hours",
		value: "4h",
		milliseconds: 4 * millisecondsHour,
	},
	{
		label: "5 hours",
		value: "5h",
		milliseconds: 5 * millisecondsHour,
	},
	{
		label: "6 hours",
		value: "6h",
		milliseconds: 6 * millisecondsHour,
	},
	{
		label: "8 hours",
		value: "8h",
		milliseconds: 8 * millisecondsHour,
	},
	{
		label: "12 hours",
		value: "12h",
		milliseconds: 12 * millisecondsHour,
	},
	{
		label: "1 day",
		value: "1d",
		milliseconds: millisecondsDay,
	},
	{
		label: "3 days",
		value: "3d",
		milliseconds: 3 * millisecondsDay,
	},
	{
		label: "5 days",
		value: "5d",
		milliseconds: 5 * millisecondsDay,
	},
	{
		label: "1 week",
		value: "1w",
		milliseconds: millisecondsWeek,
	},
	{
		label: "1.5 weeks",
		value: "1.5w",
		milliseconds: 1.5 * millisecondsWeek,
	},
	{
		label: "2 weeks",
		value: "2w",
		milliseconds: 2 * millisecondsWeek,
	},
	{
		label: "3 weeks",
		value: "3w",
		milliseconds: 3 * millisecondsWeek,
	},
	{
		label: "1 month",
		value: "1M",
		milliseconds: millisecondsMonth,
	},
	{
		label: "1.5 months",
		value: "1.5M",
		milliseconds: 1.5 * millisecondsMonth,
	},
	{
		label: "2 months",
		value: "2M",
		milliseconds: 2 * millisecondsMonth,
	},
	{
		label: "3 months",
		value: "3M",
		milliseconds: 3 * millisecondsMonth,
	},
	{
		label: "4 months",
		value: "4M",
		milliseconds: 4 * millisecondsMonth,
	},
	{
		label: "6 months",
		value: "6M",
		milliseconds: 6 * millisecondsMonth,
	},
	{
		label: "1 year",
		value: "1y",
		milliseconds: millisecondsYear,
	},
];

export const formats: SelectProps["data"] = [
	{
		label: "Random",
		value: "random",
	},
	{
		label: "Date",
		value: "date",
	},
	{
		label: "UUID",
		value: "uuid",
	},
	{
		label: "Use File Name",
		value: "name",
	},
	{
		label: "Gfycat-style Name",
		value: "gfycat",
	},
];

export const avaibleTextMimetypes: SelectProps["data"] = [
	{
		label: "HTML",
		value: "html",
		mimetype: "text/x-zipline-html",
	},
	{
		label: "CSS",
		value: "css",
		mimetype: "text/x-zipline-css",
	},
	{
		label: "C++",
		value: "cpp",
		mimetype: "text/x-zipline-c++src",
	},
	{
		label: "JavaScript",
		value: "js",
		mimetype: "text/x-zipline-javascript",
	},
	{
		label: "Python",
		value: "py",
		mimetype: "text/x-zipline-python",
	},
	{
		label: "Ruby",
		value: "rb",
		mimetype: "text/x-zipline-ruby",
	},
	{
		label: "Java",
		value: "java",
		mimetype: "text/x-zipline-java",
	},
	{
		label: "Markdown",
		value: "md",
		mimetype: "text/x-zipline-markdown",
	},
	{
		label: "C",
		value: "c",
		mimetype: "text/x-zipline-csrc",
	},
	{
		label: "PHP",
		value: "php",
		mimetype: "text/x-zipline-httpd-php",
	},
	{
		label: "Sass",
		value: "sass",
		mimetype: "text/x-zipline-sass",
	},
	{
		label: "SCSS",
		value: "scss",
		mimetype: "text/x-zipline-scss",
	},
	{
		label: "Swift",
		value: "swift",
		mimetype: "text/x-zipline-swift",
	},
	{
		label: "TypeScript",
		value: "ts",
		mimetype: "text/x-zipline-typescript",
	},
	{
		label: "Go",
		value: "go",
		mimetype: "text/x-zipline-go",
	},
	{
		label: "Rust",
		value: "rs",
		mimetype: "text/x-zipline-rustsrc",
	},
	{
		label: "Bash",
		value: "sh",
		mimetype: "text/x-zipline-sh",
	},
	{
		label: "JSON",
		value: "json",
		mimetype: "text/x-zipline-json",
	},
	{
		label: "PowerShell",
		value: "ps1",
		mimetype: "text/x-zipline-powershell",
	},
	{
		label: "SQL",
		value: "sql",
		mimetype: "text/x-zipline-sql",
	},
	{
		label: "YAML",
		value: "yaml",
		mimetype: "text/x-zipline-yaml",
	},
	{
		label: "Dockerfile",
		value: "dockerfile",
		mimetype: "text/x-zipline-dockerfile",
	},
	{
		label: "Lua",
		value: "lua",
		mimetype: "text/x-zipline-lua",
	},
	{
		label: "NGINX Config File",
		value: "conf",
		mimetype: "text/x-zipline-nginx-conf",
	},
	{
		label: "Perl",
		value: "pl",
		mimetype: "text/x-zipline-perl",
	},
	{
		label: "R",
		value: "r",
		mimetype: "text/x-zipline-rsrc",
	},
	{
		label: "Scala",
		value: "scala",
		mimetype: "text/x-zipline-scala",
	},
	{
		label: "Groovy",
		value: "groovy",
		mimetype: "text/x-zipline-groovy",
	},
	{
		label: "Kotlin",
		value: "kt",
		mimetype: "text/x-zipline-kotlin",
	},
	{
		label: "Haskell",
		value: "hs",
		mimetype: "text/x-zipline-haskell",
	},
	{
		label: "Elixir",
		value: "ex",
		mimetype: "text/x-zipline-elixir",
	},
	{
		label: "Vim",
		value: "vim",
		mimetype: "text/x-zipline-vim",
	},
	{
		label: "MATLAB",
		value: "m",
		mimetype: "text/x-zipline-matlab",
	},
	{
		label: "Dart",
		value: "dart",
		mimetype: "text/x-zipline-dart",
	},
	{
		label: "Handlebars",
		value: "hbs",
		mimetype: "text/x-zipline-handlebars-template",
	},
	{
		label: "HCL",
		value: "hcl",
		mimetype: "text/x-zipline-hcl",
	},
	{
		label: "HTTP",
		value: "http",
		mimetype: "text/x-zipline-http",
	},
	{
		label: "INI",
		value: "ini",
		mimetype: "text/x-zipline-ini",
	},
	{
		label: "JSX",
		value: "jsx",
		mimetype: "text/x-zipline-jsx",
	},
	{
		label: "CoffeeScript",
		value: "coffee",
		mimetype: "text/x-zipline-coffeescript",
	},
	{
		label: "LaTeX (KaTeX)",
		value: "tex",
		mimetype: "text/x-zipline-latex",
	},
	{
		label: "Plain Text",
		value: "txt",
		mimetype: "text/x-zipline-plain",
	},
];
