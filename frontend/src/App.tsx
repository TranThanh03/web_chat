import { TooltipProvider } from "@/components/ui/tooltip";
import { Toaster } from "@/components/ui/sonner";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import RouterCustom from "./router/RouterCustom";

const queryClient = new QueryClient();

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <TooltipProvider>
                <Toaster position="bottom-left"/>
                <RouterCustom />
            </TooltipProvider>
        </QueryClientProvider>
    );
}

export default App;